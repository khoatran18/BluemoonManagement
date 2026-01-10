import React, { useCallback, useEffect, useMemo, useState } from "react";
import LoadingSpinner from "../../Components/LoadingSpinner/LoadingSpinner";
import Tag from "../../Components/Tag/Tag";
import Button from "../../Components/Button/Button";
import { PaymentHistoriesModal } from "../../Components/PaymentHistoriesModal/PaymentHistoriesModal";
import { getApartmentFeeStatus } from "../../api/feeCollectApi";
import { getMe } from "../../api/authApi";
import { usePersistentState } from "../../hooks/usePersistentState";
import { VOLUNTARY_FEE_TYPE_ID } from "../../constants/feeTypeIds";
import "../FeeCollect/FeeCollect.css";
import "../Apartment/ApartmentManagement.css";

function toNumber(value) {
  const num = Number(value);
  return Number.isFinite(num) ? num : 0;
}

function formatCurrencyVND(value) {
  const num = toNumber(value);
  try {
    return new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
      maximumFractionDigits: 0,
    }).format(num);
  } catch {
    return `${num} VND`;
  }
}

function isPastDue(dateString) {
  if (!dateString) return false;
  const d = new Date(dateString);
  if (Number.isNaN(d.getTime())) return false;
  return d.getTime() < Date.now();
}

function isInCurrentMonth(dateString) {
  if (!dateString) return false;
  const d = new Date(dateString);
  if (Number.isNaN(d.getTime())) return false;
  const now = new Date();
  return d.getFullYear() === now.getFullYear() && d.getMonth() === now.getMonth();
}

function groupFeesByCategory(fees) {
  const map = new Map();
  for (const fee of fees || []) {
    const categoryId = fee?.fee_category_id;
    const categoryName = fee?.fee_category_name;
    const key = String(categoryId ?? categoryName ?? "unknown");
    const label = categoryName || (categoryId !== undefined && categoryId !== null ? `Danh mục ${categoryId}` : "Danh mục");
    if (!map.has(key)) {
      map.set(key, { key, label, fees: [] });
    }
    map.get(key).fees.push(fee);
  }
  return Array.from(map.values()).sort((a, b) => a.label.localeCompare(b.label, "vi"));
}

const feeTypeMap = {
  1: { key: "obligatory", label: "Định kỳ" },
  2: { key: "voluntary", label: "Tự nguyện" },
  3: { key: "impromptu", label: "Đột xuất" },
};

function toApartmentId(value) {
  const num = Number(String(value || "").trim());
  return Number.isFinite(num) && num > 0 ? num : null;
}

export default function MyFeeStatus() {
  const [storedId, setStoredId] = usePersistentState("citizen.apartmentId", "");
  const [inputId, setInputId] = useState(String(storedId || ""));

  const apartmentId = useMemo(() => toApartmentId(storedId), [storedId]);

  const [meLoading, setMeLoading] = useState(true);
  const [me, setMe] = useState(null);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [data, setData] = useState(null);
  const [isPayHistoriesOpen, setIsPayHistoriesOpen] = useState(false);
  const [feesVisible, setFeesVisible] = useState(true);

  useEffect(() => {
    let mounted = true;
    (async () => {
      setMeLoading(true);
      try {
        const me = await getMe();
        const meApartmentId = me?.apartment_id;
        if (!mounted) return;

        setMe(me);
        if (meApartmentId !== undefined && meApartmentId !== null && String(meApartmentId) !== "") {
          setStoredId(String(meApartmentId));
          setInputId(String(meApartmentId));
        }
      } catch {
        // Fallback to manual input.
      } finally {
        if (mounted) setMeLoading(false);
      }
    })();
    return () => {
      mounted = false;
    };
  }, []);

  const fetchStatus = useCallback(async () => {
    if (!apartmentId) {
      setData(null);
      return;
    }

    setLoading(true);
    setError(null);
    try {
      const resp = await getApartmentFeeStatus(apartmentId);
      setData(resp);
    } catch (e) {
      setError(e);
      setData(null);
    } finally {
      setLoading(false);
    }
  }, [apartmentId]);

  useEffect(() => {
    fetchStatus();
  }, [fetchStatus]);

  const onSave = () => {
    setStoredId(String(inputId || "").trim());
  };

  const meApartmentId = useMemo(() => toApartmentId(me?.apartment_id), [me]);
  const hasAutoApartment = !!meApartmentId;

  const unpaidFees = data?.unpaid_fees || [];
  const voluntaryFees = useMemo(
    () => unpaidFees.filter((f) => String(f?.fee_type_id) === VOLUNTARY_FEE_TYPE_ID),
    [unpaidFees]
  );
  const nonVoluntaryUnpaidFees = useMemo(
    () => unpaidFees.filter((f) => String(f?.fee_type_id) !== VOLUNTARY_FEE_TYPE_ID),
    [unpaidFees]
  );

  const pastDueFees = useMemo(
    () => nonVoluntaryUnpaidFees.filter((f) => isPastDue(f?.expiry_date)),
    [nonVoluntaryUnpaidFees]
  );
  const currentMonthFees = useMemo(
    () => nonVoluntaryUnpaidFees.filter((f) => !isPastDue(f?.expiry_date) && isInCurrentMonth(f?.expiry_date)),
    [nonVoluntaryUnpaidFees]
  );
  const upcomingFees = useMemo(
    () => nonVoluntaryUnpaidFees.filter((f) => !isPastDue(f?.expiry_date) && !isInCurrentMonth(f?.expiry_date)),
    [nonVoluntaryUnpaidFees]
  );

  const pastDueCount = pastDueFees.length;
  const unpaidTotal = nonVoluntaryUnpaidFees.reduce((sum, f) => sum + toNumber(f?.fee_amount), 0);

  const renderFeeRow = (fee, { overdue = false } = {}) => {
    const feeType = feeTypeMap[fee?.fee_type_id];
    return (
      <div
        className={`fee-status-fee ${overdue ? "fee-status-fee--overdue" : ""}`}
        key={fee?.fee_id || `${fee?.fee_name}-${fee?.expiry_date}`}
      >
        <div className="fee-status-fee-main">
          <div className="fee-status-fee-title-wrap">
            <div className="fee-status-fee-title">{fee?.fee_name || "Khoản phí"}</div>
            {feeType ? (
              <Tag variant="Fee" type={feeType.key} className="fee-status-fee-type-tag">
                {feeType.label}
              </Tag>
            ) : (
              <div className="fee-status-fee-type-fallback">{fee?.fee_type_name || fee?.fee_type_id || ""}</div>
            )}
          </div>

          <div className={`fee-status-fee-amount ${overdue ? "fee-status-fee-amount--overdue" : ""}`}>
            {formatCurrencyVND(fee?.fee_amount)}
          </div>
        </div>

        <div className="fee-status-fee-meta">
          Danh mục: {fee?.fee_category_name || fee?.fee_category_id || "-"} · Hết hạn: {fee?.expiry_date ? String(fee.expiry_date).slice(0, 10) : "-"}
        </div>
      </div>
    );
  };

  const renderCategoryDropdown = (categoryGroup, { overdue = false } = {}) => {
    const total = categoryGroup.fees.reduce((sum, fee) => sum + toNumber(fee?.fee_amount), 0);
    return (
      <details className="fee-status-category" key={categoryGroup.key}>
        <summary className="fee-status-category-summary">
          <span className="fee-status-category-title">{categoryGroup.label}</span>
          <span className="fee-status-category-meta">
            {categoryGroup.fees.length} khoản · {formatCurrencyVND(total)}
          </span>
        </summary>
        <div className="fee-status-category-body">
          {categoryGroup.fees.map((fee) => renderFeeRow(fee, { overdue }))}
        </div>
      </details>
    );
  };

  return (
    <div className="apartment-management-container fee-status-page">
      <div className="fee-status-header">
        <div>
          <div className="fee-status-title">Tình trạng phí</div>
          <div className="fee-status-subtitle">{apartmentId ? `Apartment ID: ${apartmentId}` : "Chưa chọn căn hộ"}</div>
        </div>
        <div className="fee-collect-actions fee-status-header-actions" style={{ gap: 8 }}>
          {!hasAutoApartment ? (
            <>
              <input
                className="fee-collect-search"
                style={{ maxWidth: 220 }}
                placeholder="Apartment ID"
                value={inputId}
                onChange={(e) => setInputId(e.target.value)}
              />
              <Button onClick={onSave}>Lưu</Button>
            </>
          ) : null}
          <Button
            className="fee-collect-btn-secondary"
            onClick={() => setIsPayHistoriesOpen(true)}
            disabled={!apartmentId}
          >
            Lịch sử thanh toán
          </Button>
          <Button variant="secondary" onClick={fetchStatus} disabled={!apartmentId}>
            Làm mới
          </Button>
        </div>
      </div>

      <PaymentHistoriesModal
        isOpen={isPayHistoriesOpen}
        onClose={() => setIsPayHistoriesOpen(false)}
        apartmentId={apartmentId}
      />

      {me && (me?.resident_id || me?.apartment_id) ? (
        <div style={{ padding: 16, paddingTop: 0, opacity: 0.85 }}>
          {me?.resident_id ? <span>Resident ID: <strong>{me.resident_id}</strong></span> : null}
          {me?.resident_id && me?.apartment_id ? <span> · </span> : null}
          {me?.apartment_id ? <span>Apartment ID: <strong>{me.apartment_id}</strong></span> : null}
        </div>
      ) : null}

      {!apartmentId ? (
        <div style={{ padding: 16 }}>
          {meLoading ? "Đang lấy thông tin người dùng…" : "Không lấy được Apartment ID từ hệ thống. Vui lòng nhập Apartment ID để xem tình trạng phí."}
        </div>
      ) : null}

      {loading ? (
        <div style={{ padding: 16 }}>
          <LoadingSpinner />
        </div>
      ) : null}

      {error ? (
        <div className="fee-status-error">Không tải được dữ liệu: {String(error?.message || error)}</div>
      ) : null}

      {!loading && !error && data ? (
        <>
          <div className="fee-status-summary" style={{ gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))" }}>
            <div className="fee-status-card fee-status-card--total">
              <div className="fee-status-card-label">Tổng phí chưa đóng</div>
              <div className="fee-status-card-value fee-status-card-value--total">{formatCurrencyVND(unpaidTotal)}</div>
              <div className="fee-status-card-sub">{nonVoluntaryUnpaidFees.length} khoản</div>
            </div>

            <div className="fee-status-card">
              <div className="fee-status-card-label">Quá hạn</div>
              <div className="fee-status-card-value">{pastDueCount}</div>
              <div className="fee-status-card-sub">khoản</div>
            </div>

            <div className="fee-status-card">
              <div className="fee-status-card-label">Số dư</div>
              <div className="fee-status-card-value">{formatCurrencyVND(data?.balance)}</div>
              <div className="fee-status-card-sub">(nếu có)</div>
            </div>
          </div>

          <div className="fee-status-section">
            <div className="fee-status-section-title">Phí chưa trả</div>

            <details
              className="fee-status-list-toggle"
              open={feesVisible}
              onToggle={(e) => setFeesVisible(Boolean(e?.currentTarget?.open))}
            >
              <summary className="fee-status-list-toggle-summary">
                {feesVisible ? "Ẩn danh sách phí" : "Hiển thị danh sách phí"}
              </summary>
            </details>

            {!feesVisible ? null : unpaidFees.length === 0 ? (
              <div className="fee-status-empty">Không có phí chưa trả.</div>
            ) : (
              <div className="fee-status-fee-list">
                {pastDueFees.length > 0 ? (
                  <>
                    <div className="fee-status-subsection-title fee-status-subsection-title--overdue">Phí quá hạn</div>
                    {groupFeesByCategory(pastDueFees).map((group) => renderCategoryDropdown(group, { overdue: true }))}
                  </>
                ) : null}

                <>
                  <div className="fee-status-subsection-title">Phí cần trả tháng này</div>
                  {currentMonthFees.length > 0 ? (
                    groupFeesByCategory(currentMonthFees).map((group) => renderCategoryDropdown(group))
                  ) : (
                    <div className="fee-status-empty">Không có phí cần trả trong tháng này.</div>
                  )}
                </>

                <>
                  <div className="fee-status-subsection-title">Phí chưa đến hạn</div>
                  {upcomingFees.length > 0 ? (
                    groupFeesByCategory(upcomingFees).map((group) => renderCategoryDropdown(group))
                  ) : (
                    <div className="fee-status-empty">Không có phí chưa đến hạn.</div>
                  )}
                </>

                <>
                  <div className="fee-status-subsection-title">Phí tự nguyện</div>
                  {voluntaryFees.length > 0 ? (
                    groupFeesByCategory(voluntaryFees).map((group) => renderCategoryDropdown(group))
                  ) : (
                    <div className="fee-status-empty">Không có phí tự nguyện.</div>
                  )}
                </>
              </div>
            )}
          </div>
        </>
      ) : null}
    </div>
  );
}
