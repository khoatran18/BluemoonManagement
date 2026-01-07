import React, { useCallback, useEffect, useMemo, useState } from "react";
import LoadingSpinner from "../../Components/LoadingSpinner/LoadingSpinner";
import Tag from "../../Components/Tag/Tag";
import Button from "../../Components/Button/Button";
import { getApartmentFeeStatus } from "../../api/feeCollectApi";
import { getMe } from "../../api/authApi";
import { usePersistentState } from "../../hooks/usePersistentState";
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
  const pastDueCount = unpaidFees.filter((f) => isPastDue(f?.expiry_date)).length;
  const unpaidTotal = unpaidFees.reduce((sum, f) => sum + toNumber(f?.fee_amount), 0);

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
          <Button variant="secondary" onClick={fetchStatus} disabled={!apartmentId}>
            Làm mới
          </Button>
        </div>
      </div>

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
              <div className="fee-status-card-sub">{unpaidFees.length} khoản</div>
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

          <div style={{ padding: 12 }}>
            <div style={{ fontWeight: 600, marginBottom: 10 }}>Các khoản chưa đóng</div>
            {unpaidFees.length === 0 ? (
              <div className="fee-status-empty">Không có khoản phí nào chưa đóng.</div>
            ) : (
              <div style={{ display: "grid", gap: 10 }}>
                {unpaidFees.map((fee) => {
                  const overdue = isPastDue(fee?.expiry_date);
                  const feeTypeId = String(fee?.fee_type_id || "");
                  const feeTypeLabel = feeTypeId === "1" ? "Định kỳ" : feeTypeId === "2" ? "Đột xuất" : feeTypeId === "3" ? "Tự nguyện" : "Phí";
                  return (
                    <div className={`fee-status-fee ${overdue ? "fee-status-fee--overdue" : ""}`} key={fee?.fee_id || `${fee?.fee_name}-${fee?.expiry_date}`}
                    >
                      <div className="fee-status-fee-main">
                        <div className="fee-status-fee-title-wrap">
                          <div className="fee-status-fee-title">{fee?.fee_name || "Khoản phí"}</div>
                          <div className="fee-status-fee-type-fallback">{feeTypeLabel}</div>
                        </div>
                        <div className={`fee-status-fee-amount ${overdue ? "fee-status-fee-amount--overdue" : ""}`}>
                          {formatCurrencyVND(fee?.fee_amount)}
                        </div>
                      </div>
                      <div className="fee-status-fee-meta">
                        <div>Hạn: {fee?.expiry_date ? String(fee.expiry_date).slice(0, 10) : ""}</div>
                        <div style={{ display: "flex", gap: 8, alignItems: "center" }}>
                          {overdue ? <Tag variant="Danger" type="overdue">Quá hạn</Tag> : <Tag variant="Success" type="ok">Còn hạn</Tag>}
                        </div>
                      </div>
                    </div>
                  );
                })}
              </div>
            )}
          </div>
        </>
      ) : null}
    </div>
  );
}
