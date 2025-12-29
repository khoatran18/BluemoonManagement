import React, { useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import Button from "../../Components/Button/Button";
import Tag from "../../Components/Tag/Tag";
import { getApartmentFeeStatus, peekApartmentFeeStatusCache } from "../../api/feeCollectApi";

import "../Fee/Fee.css";
import "./FeeCollect.css";
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

const feeTypeMap = {
  1: { key: "obligatory", label: "Định kỳ" },
  2: { key: "voluntary", label: "Tự nguyện" },
  3: { key: "impromptu", label: "Đột xuất" },
};

export default function ApartmentFeeStatus() {
  const { id } = useParams();
  const navigate = useNavigate();
  const location = useLocation();

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [data, setData] = useState(null);

  useEffect(() => {
    let cancelled = false;

    const stateData = location?.state?.feeStatus;
    if (stateData && String(stateData?.apartment_id) === String(id)) {
      setData(stateData);
      setError(null);
      setLoading(false);
      return () => {
        cancelled = true;
      };
    }

    const cached = peekApartmentFeeStatusCache(id);
    if (cached) {
      setData(cached);
      setError(null);
      setLoading(false);
      return () => {
        cancelled = true;
      };
    }

    async function fetchStatus() {
      setLoading(true);
      setError(null);
      setData(null);

      try {
        const resp = await getApartmentFeeStatus(id, { useCache: true });
        if (!cancelled) {
          setData(resp);
          navigate(".", {
            replace: true,
            state: { ...(location?.state || {}), feeStatus: resp },
          });
        }
      } catch (e) {
        if (!cancelled) setError(e);
      } finally {
        if (!cancelled) setLoading(false);
      }
    }

    fetchStatus();

    return () => {
      cancelled = true;
    };
  }, [id]);

  const unpaidFees = data?.unpaid_fees || [];
  const adjustments = data?.adjustments || [];
  const extraAdjustments = data?.extra_adjustments || [];

  const pastDueFees = useMemo(() => unpaidFees.filter((f) => isPastDue(f?.expiry_date)), [unpaidFees]);
  const currentMonthFees = useMemo(
    () => unpaidFees.filter((f) => !isPastDue(f?.expiry_date) && isInCurrentMonth(f?.expiry_date)),
    [unpaidFees]
  );

  const totals = useMemo(() => {
    const unpaidTotal = unpaidFees.reduce((sum, fee) => sum + toNumber(fee?.fee_amount), 0);
    const pastDueTotal = pastDueFees.reduce((sum, fee) => sum + toNumber(fee?.fee_amount), 0);
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
      adjustmentsTotal,
      increaseTotal,
      decreaseTotal,
      finalAmount,
    };
  }, [unpaidFees, pastDueFees, adjustments, extraAdjustments]);

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

  return (
    <div className="apartment-management-container fee-status-page">
      <div className="fee-status-header">
        <div>
          <div className="fee-status-title">Tình trạng phí căn hộ</div>
          <div className="fee-status-subtitle">Apartment ID: {id}</div>
        </div>

        <Button className="fee-collect-btn-secondary" icon={null} onClick={() => navigate(-1)}>
          Quay lại
        </Button>
      </div>

      {loading ? (
        <div className="fee-spinner">
          <div></div>
        </div>
      ) : error ? (
        <div className="fee-status-error">
          Không tải được dữ liệu: {String(error?.message || error)}
        </div>
      ) : (
        <>
          <div className="fee-status-summary">
            <div className="fee-status-card">
              <div className="fee-status-card-label">Tổng phí chưa trả</div>
              <div className="fee-status-card-value">{formatCurrencyVND(totals.unpaidTotal)}</div>
            </div>
            <div className="fee-status-card fee-status-card--overdue">
              <div className="fee-status-card-label">Phí quá hạn</div>
              <div className="fee-status-card-value fee-status-card-value--overdue">
                {formatCurrencyVND(totals.pastDueTotal)}
              </div>
              <div className="fee-status-card-sub">{totals.pastDueCount} khoản</div>
            </div>
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
            <div className="fee-status-card">
              <div className="fee-status-card-label">Thành tiền</div>
              <div className="fee-status-card-value">{formatCurrencyVND(totals.finalAmount)}</div>
            </div>
          </div>

          <div className="fee-status-section">
            <div className="fee-status-section-title">Phí chưa trả</div>

            {unpaidFees.length === 0 ? (
              <div className="fee-status-empty">Không có phí chưa trả.</div>
            ) : (
              <div className="fee-status-fee-list">
                {pastDueFees.length > 0 ? (
                  <>
                    <div className="fee-status-subsection-title fee-status-subsection-title--overdue">Phí quá hạn</div>
                    {pastDueFees.map((fee) => renderFeeRow(fee, { overdue: true }))}
                  </>
                ) : null}

                <>
                  <div className="fee-status-subsection-title">Phí cần trả tháng này</div>
                  {currentMonthFees.length > 0 ? (
                    currentMonthFees.map((fee) => renderFeeRow(fee))
                  ) : (
                    <div className="fee-status-empty">Không có phí cần trả trong tháng này.</div>
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
