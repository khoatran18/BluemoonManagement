import React, { useCallback, useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import Button from "../../Components/Button/Button";
import Tag from "../../Components/Tag/Tag";
import LoadingSpinner from "../../Components/LoadingSpinner/LoadingSpinner";
import { getApartmentFeeStatus } from "../../api/feeCollectApi";
import { PaymentHistoriesModal } from "../../Components/PaymentHistoriesModal/PaymentHistoriesModal";
import "./FeeCollect.css";
import "../Apartment/ApartmentManagement.css";

const EMPTY_ARRAY = Object.freeze([]);

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

function adjustmentSignedAmount(adj) {
  const amount = toNumber(adj?.adjustment_amount);
  const type = String(adj?.adjustment_type || "").toLowerCase();
  if (type.includes("decrease")) return -amount;
  if (type.includes("increase")) return amount;
  // fallback: treat unknown as increase
  return amount;
}

function adjustmentKind(adj) {
  const type = String(adj?.adjustment_type || "").toLowerCase();
  if (type.includes("decrease")) return "decrease";
  if (type.includes("increase")) return "increase";
  return "unknown";
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
  2: { key: "impromptu", label: "Đột xuất" },
  3: { key: "voluntary", label: "Tự nguyện" },
};

export default function ApartmentFeeStatus() {
  const { id } = useParams();
  const navigate = useNavigate();
  const location = useLocation();

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [data, setData] = useState(null);
  const [feesVisible, setFeesVisible] = useState(true);
  const [accountingVisible, setAccountingVisible] = useState(true);
  const [useBalance, setUseBalance] = useState(false);

  const [isPayHistoriesOpen, setIsPayHistoriesOpen] = useState(false);

  const fetchStatus = useCallback(
    async () => {
      setLoading(true);
      setError(null);

      try {
        const resp = await getApartmentFeeStatus(id);
        setData(resp);
      } catch (e) {
        setError(e);
      } finally {
        setLoading(false);
      }
    },
    [id]
  );

  useEffect(() => {
    fetchStatus();
  }, [id, fetchStatus]);

  const unpaidFees = useMemo(
    () => (Array.isArray(data?.unpaid_fees) ? data.unpaid_fees : EMPTY_ARRAY),
    [data]
  );
  const adjustments = useMemo(
    () => (Array.isArray(data?.adjustments) ? data.adjustments : EMPTY_ARRAY),
    [data]
  );
  const extraAdjustments = useMemo(
    () => (Array.isArray(data?.extra_adjustments) ? data.extra_adjustments : EMPTY_ARRAY),
    [data]
  );

  const paidTotal = data?.total_paid;
  const balance = data?.balance;
  const balanceCredit = useMemo(() => toNumber(balance), [balance]);
  const canUseBalance = balanceCredit > 0;

  const voluntaryFees = useMemo(() => unpaidFees.filter((f) => String(f?.fee_type_id) === "3"), [unpaidFees]);
  const nonVoluntaryUnpaidFees = useMemo(() => unpaidFees.filter((f) => String(f?.fee_type_id) !== "3"), [unpaidFees]);

  const pastDueFees = useMemo(() => nonVoluntaryUnpaidFees.filter((f) => isPastDue(f?.expiry_date)), [nonVoluntaryUnpaidFees]);
  const currentMonthFees = useMemo(
    () => nonVoluntaryUnpaidFees.filter((f) => !isPastDue(f?.expiry_date) && isInCurrentMonth(f?.expiry_date)),
    [nonVoluntaryUnpaidFees]
  );
  const upcomingFees = useMemo(
    () => nonVoluntaryUnpaidFees.filter((f) => !isPastDue(f?.expiry_date) && !isInCurrentMonth(f?.expiry_date)),
    [nonVoluntaryUnpaidFees]
  );

  const totals = useMemo(() => {
    const unpaidTotal = nonVoluntaryUnpaidFees.reduce((sum, fee) => sum + toNumber(fee?.fee_amount), 0);
    const pastDueTotal = pastDueFees.reduce((sum, fee) => sum + toNumber(fee?.fee_amount), 0);
    const currentMonthTotal = currentMonthFees.reduce((sum, fee) => sum + toNumber(fee?.fee_amount), 0);
    const voluntaryTotal = voluntaryFees.reduce((sum, fee) => sum + toNumber(fee?.fee_amount), 0);
    const allAdjustments = [...adjustments, ...extraAdjustments];
    const adjustmentsTotal = allAdjustments.reduce((sum, adj) => sum + adjustmentSignedAmount(adj), 0);

    const increaseTotal = allAdjustments.reduce((sum, adj) => {
      const kind = adjustmentKind(adj);
      const amt = Math.abs(toNumber(adj?.adjustment_amount));
      if (kind === "increase" || kind === "unknown") return sum + amt;
      return sum;
    }, 0);

    const decreaseTotal = allAdjustments.reduce((sum, adj) => {
      const kind = adjustmentKind(adj);
      const amt = Math.abs(toNumber(adj?.adjustment_amount));
      if (kind === "decrease") return sum + amt;
      return sum;
    }, 0);
    const finalAmount = unpaidTotal + adjustmentsTotal;

    return {
      unpaidTotal,
      pastDueTotal,
      pastDueCount: pastDueFees.length,
      currentMonthTotal,
      currentMonthCount: currentMonthFees.length,
      voluntaryTotal,
      voluntaryCount: voluntaryFees.length,
      adjustmentsTotal,
      increaseTotal,
      decreaseTotal,
      finalAmount,
    };
  }, [nonVoluntaryUnpaidFees, voluntaryFees, pastDueFees, currentMonthFees, adjustments, extraAdjustments]);

  useEffect(() => {
    if (!canUseBalance && useBalance) setUseBalance(false);
  }, [canUseBalance, useBalance]);

  const finalAmountAfterBalance = useMemo(() => {
    if (!useBalance || !canUseBalance) return toNumber(totals.finalAmount);
    return Math.max(0, toNumber(totals.finalAmount) - balanceCredit);
  }, [useBalance, canUseBalance, totals.finalAmount, balanceCredit]);

  const remainingBalanceCredit = useMemo(() => {
    if (useBalance && canUseBalance) return 0;
    return balanceCredit;
  }, [useBalance, canUseBalance, balanceCredit]);

  const renderAdjustmentRow = (adj, keyPrefix = "adj") => {
    const signed = adjustmentSignedAmount(adj);
    const type = String(adj?.adjustment_type || "").toLowerCase();
    const signLabel = type.includes("decrease") ? "-" : "+";
    const amountVariant = type.includes("decrease") ? "decrease" : type.includes("increase") ? "increase" : "";

    return (
      <div className="fee-status-adjustment" key={`${keyPrefix}-${adj?.adjustment_id ?? Math.random()}`}>
        <div className="fee-status-adjustment-main">
          <div className="fee-status-adjustment-title">
            #{adj?.adjustment_id} · {adj?.reason || "(Không có lý do)"}
          </div>
          <div className={`fee-status-adjustment-amount ${amountVariant ? `fee-status-adjustment-amount--${amountVariant}` : ""}`}>
            {signLabel} {formatCurrencyVND(Math.abs(signed))}
          </div>
        </div>
        <div className="fee-status-adjustment-meta">
            Ngày hiệu lực: {adj?.effective_date || "-"} · Ngày hết hạn: {adj?.expiry_date || "-"}
        </div>
      </div>
    );
  };

  const renderFeeRow = (fee, { overdue = false } = {}) => {
    const feeSpecificAdjustments = adjustments.filter((a) => String(a?.fee_id) === String(fee?.fee_id));
    const feeType = feeTypeMap[fee?.fee_type_id];

    return (
      <div className={`fee-status-fee ${overdue ? "fee-status-fee--overdue" : ""}`} key={fee?.fee_id}>
        <div className="fee-status-fee-main">
          <div className="fee-status-fee-title-wrap">
            <div className="fee-status-fee-title">{fee?.fee_name}</div>
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
           Danh mục: {fee?.fee_category_name || fee?.fee_category_id} · Hết hạn: {fee?.expiry_date || "-"}
        </div>

        <details className="fee-status-dropdown">
          <summary className="fee-status-dropdown-summary">Điều chỉnh áp dụng</summary>

          <div className="fee-status-dropdown-body">
            {feeSpecificAdjustments.length === 0 && extraAdjustments.length === 0 ? (
              <div className="fee-status-empty">Không có điều chỉnh.</div>
            ) : (
              <>
                {feeSpecificAdjustments.length > 0 ? (
                  <div className="fee-status-adjustment-group">
                    <div className="fee-status-adjustment-group-title">Theo fee</div>
                    {feeSpecificAdjustments.map((adj) => renderAdjustmentRow(adj, "fee"))}
                  </div>
                ) : null}

                {extraAdjustments.length > 0 ? (
                  <div className="fee-status-adjustment-group">
                    <div className="fee-status-adjustment-group-title">Theo căn hộ (fee_id = -1)</div>
                    {extraAdjustments.map((adj) => renderAdjustmentRow(adj, "extra"))}
                  </div>
                ) : null}
              </>
            )}
          </div>
        </details>
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
          <div className="fee-status-title">Tình trạng phí căn hộ</div>
          <div className="fee-status-subtitle">Apartment ID: {id}</div>
        </div>

        <div className="fee-collect-actions fee-status-header-actions">
          <Button
            className="fee-collect-btn-secondary fee-collect-btn-with-icon"
            icon={
              <svg
                width="16"
                height="16"
                viewBox="0 0 24 24"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
                aria-hidden="true"
              >
                <path
                  d="M15 18l-6-6 6-6"
                  stroke="currentColor"
                  strokeWidth="2"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                />
              </svg>
            }
            onClick={() => navigate(-1)}
          >
            Quay lại
          </Button>

          <Button
            className="fee-collect-btn-secondary"
            onClick={() => setIsPayHistoriesOpen(true)}
          >
            Lịch sử thanh toán
          </Button>
          <Button
            className="fee-collect-btn-secondary fee-collect-btn-with-icon"
            icon={
              <svg
                width="16"
                height="16"
                viewBox="0 0 24 24"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
                aria-hidden="true"
              >
                <path
                  d="M20 12a8 8 0 1 1-2.343-5.657"
                  stroke="currentColor"
                  strokeWidth="2"
                  strokeLinecap="round"
                />
                <path
                  d="M20 4v6h-6"
                  stroke="currentColor"
                  strokeWidth="2"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                />
              </svg>
            }
            onClick={() => fetchStatus({ force: true })}
          >
            Tải lại
          </Button>
          <Button
            className="fee-collect-btn-primary fee-collect-btn-with-icon"
            icon={
              <svg
                width="16"
                height="16"
                viewBox="0 0 24 24"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
                aria-hidden="true"
              >
                <path
                  d="M6 6h15l-1.5 9h-13z"
                  stroke="currentColor"
                  strokeWidth="2"
                  strokeLinejoin="round"
                />
                <path
                  d="M6 6l-2-2H2"
                  stroke="currentColor"
                  strokeWidth="2"
                  strokeLinecap="round"
                />
                <path
                  d="M7 20a1 1 0 1 0 0-2 1 1 0 0 0 0 2Zm12 0a1 1 0 1 0 0-2 1 1 0 0 0 0 2Z"
                  fill="currentColor"
                />
              </svg>
            }
            onClick={() =>
              navigate(`/fee-collection/apartment/${id}/collect`, {
                state: { apartmentId: id, feeStatus: data || location?.state?.feeStatus || null },
              })
            }
          >
            Thu phí
          </Button>
        </div>
      </div>

      <PaymentHistoriesModal
        isOpen={isPayHistoriesOpen}
        onClose={() => setIsPayHistoriesOpen(false)}
        apartmentId={id}
      />

      {loading ? (
        <LoadingSpinner />
      ) : error ? (
        <div className="fee-status-error">
          Không tải được dữ liệu: {String(error?.message || error)}
        </div>
      ) : (
        <>
          <div className="fee-status-top">
            <details
              className="fee-status-accounting-toggle"
              open={accountingVisible}
              onToggle={(e) => setAccountingVisible(Boolean(e?.currentTarget?.open))}
            >
              <summary className="fee-status-accounting-toggle-summary">
                {accountingVisible ? "Ẩn" : "Thêm thông tin"}
              </summary>
            </details>

            {accountingVisible ? (
              <div className="fee-status-accounting">
                <div className="fee-status-card">
                  <div className="fee-status-card-label">Đã thanh toán</div>
                  <div className="fee-status-card-value">{formatCurrencyVND(paidTotal)}</div>
                </div>
                <div className="fee-status-card">
                  <div className="fee-status-card-label">Số dư</div>
                  <div className="fee-status-card-value">{formatCurrencyVND(balance)}</div>
                </div>
              </div>
            ) : null}
          </div>

          <div className="fee-status-summary">
            <div className="fee-status-card fee-status-card--overdue">
              <div className="fee-status-card-label">Phí quá hạn</div>
              <div className="fee-status-card-value fee-status-card-value--overdue">{formatCurrencyVND(totals.pastDueTotal)}</div>
              <div className="fee-status-card-sub">{totals.pastDueCount} khoản</div>
            </div>
            <div className="fee-status-card">
              <div className="fee-status-card-label">Phí cần trả tháng này</div>
              <div className="fee-status-card-value">{formatCurrencyVND(totals.currentMonthTotal)}</div>
              <div className="fee-status-card-sub">{totals.currentMonthCount} khoản</div>
            </div>
          </div>

          <div className="fee-status-unpaid-total">
            <div className="fee-status-card fee-status-card--voluntary">
              <div className="fee-status-card-label">Khoản tự nguyện</div>
              <div className="fee-status-card-value">{formatCurrencyVND(totals.voluntaryTotal)}</div>
              <div className="fee-status-card-sub">{totals.voluntaryCount} khoản</div>
            </div>
          </div>

          <div className="fee-status-adjustments">
            <div className="fee-status-card">
              <div className="fee-status-card-label">Tổng điều chỉnh</div>
              <div className="fee-status-card-value">{formatCurrencyVND(totals.adjustmentsTotal)}</div>
              <div className="fee-status-card-breakdown">
                <div className="fee-status-card-breakdown-row">
                  <span>Tăng</span>
                  <span className="fee-status-text--increase">+ {formatCurrencyVND(totals.increaseTotal)}</span>
                </div>
                <div className="fee-status-card-breakdown-row">
                  <span>Giảm</span>
                  <span className="fee-status-text--decrease">- {formatCurrencyVND(totals.decreaseTotal)}</span>
                </div>
              </div>
            </div>
          </div>

          <hr className="fee-status-divider" />

          <div className="fee-status-total">
            <div
              className={`fee-status-card fee-status-card--total ${useBalance && canUseBalance ? "is-balance-applied" : ""}`}
            >
              <div className="fee-status-card-label fee-status-card-label--with-control">
                <span>Thành tiền</span>
                <label className={`fee-status-balance-toggle ${!canUseBalance ? "is-disabled" : ""}`}>
                  <input
                    type="checkbox"
                    checked={useBalance}
                    disabled={!canUseBalance}
                    onChange={(e) => setUseBalance(Boolean(e?.target?.checked))}
                  />
                  <span>Dùng số dư</span>
                </label>
              </div>

              {useBalance && canUseBalance ? (
                <div className="fee-status-total-values">
                  <div className="fee-status-total-old">{formatCurrencyVND(totals.finalAmount)}</div>
                  <div className="fee-status-card-value fee-status-card-value--total">{formatCurrencyVND(finalAmountAfterBalance)}</div>
                </div>
              ) : (
                <div className="fee-status-card-value fee-status-card-value--total">{formatCurrencyVND(totals.finalAmount)}</div>
              )}
            </div>

            <div className="fee-status-card fee-status-card--remaining">
              <div className="fee-status-card-label">Số dư còn lại</div>
              <div className="fee-status-card-value fee-status-card-value--remaining">{formatCurrencyVND(remainingBalanceCredit)}</div>
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
      )}
    </div>
  );
}
