import React, { useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import Button from "../../Components/Button/Button";
import {
  clearApartmentFeeStatusCache,
  getApartmentFeeStatus,
  updateApartmentFeeStatus,
} from "../../api/feeCollectApi";

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

export default function CollectFee() {
  const { id } = useParams();
  const navigate = useNavigate();
  const location = useLocation();

  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);
  const [data, setData] = useState(null);
  const [selectedFeeIds, setSelectedFeeIds] = useState(() => new Set());
  const [useBalance, setUseBalance] = useState(false);

  useEffect(() => {
    let cancelled = false;

    async function load() {
      setLoading(true);
      setError(null);

      try {
        const stateData = location?.state?.feeStatus;
        if (stateData && String(stateData?.apartment_id) === String(id)) {
          if (!cancelled) setData(stateData);
          return;
        }

        const resp = await getApartmentFeeStatus(id, { useCache: true, force: false });
        if (!cancelled) setData(resp);
      } catch (e) {
        if (!cancelled) setError(e);
      } finally {
        if (!cancelled) setLoading(false);
      }
    }

    load();
    return () => {
      cancelled = true;
    };
  }, [id, location?.state?.feeStatus]);

  const unpaidFees = data?.unpaid_fees || [];
  const balanceCredit = useMemo(() => toNumber(data?.balance), [data?.balance]);
  const canUseBalance = balanceCredit > 0;

  const selectedFees = useMemo(() => {
    const selected = new Set(Array.from(selectedFeeIds));
    return unpaidFees.filter((f) => selected.has(String(f?.fee_id)));
  }, [unpaidFees, selectedFeeIds]);

  const selectedTotal = useMemo(() => selectedFees.reduce((sum, f) => sum + toNumber(f?.fee_amount), 0), [selectedFees]);

  useEffect(() => {
    if (!canUseBalance && useBalance) setUseBalance(false);
  }, [canUseBalance, useBalance]);

  const appliedFromBalance = useMemo(() => {
    if (!useBalance || !canUseBalance) return 0;
    return Math.min(balanceCredit, selectedTotal);
  }, [useBalance, canUseBalance, balanceCredit, selectedTotal]);

  const payableNow = useMemo(() => Math.max(0, selectedTotal - appliedFromBalance), [selectedTotal, appliedFromBalance]);
  const remainingBalanceAfter = useMemo(
    () => (useBalance && canUseBalance ? Math.max(0, balanceCredit - appliedFromBalance) : balanceCredit),
    [useBalance, canUseBalance, balanceCredit, appliedFromBalance]
  );

  const toggleFee = (feeId) => {
    const key = String(feeId);
    setSelectedFeeIds((prev) => {
      const next = new Set(prev);
      if (next.has(key)) next.delete(key);
      else next.add(key);
      return next;
    });
  };

  const selectAll = () => {
    setSelectedFeeIds(new Set(unpaidFees.map((f) => String(f?.fee_id))));
  };

  const clearSelection = () => {
    setSelectedFeeIds(new Set());
  };

  const onSubmit = async () => {
    if (submitting) return;
    if (selectedFeeIds.size === 0) return;

    setSubmitting(true);
    setError(null);

    try {
      const existingTotalPaid = toNumber(data?.total_paid);
      const existingBalance = toNumber(data?.balance);

      const paid_fees = selectedFees.map((f) => ({ fee_id: f?.fee_id }));

      // Keep the rest as unpaid so backend does NOT clear all unpaid fees.
      const unpaid_fees = unpaidFees
        .filter((f) => !selectedFeeIds.has(String(f?.fee_id)))
        .map((f) => ({ fee_id: f?.fee_id }));

      const payload = {
        // Total fees collected (regardless of whether paid by cash or balance)
        total_paid: existingTotalPaid + selectedTotal,
        // If using balance, deduct the applied part from remaining balance
        balance: useBalance ? remainingBalanceAfter : existingBalance,
        paid_fees,
        unpaid_fees,
      };

      await updateApartmentFeeStatus(id, payload);
      clearApartmentFeeStatusCache(id);

      navigate(`/fee-collection/apartment/${id}/status`, { replace: true });
    } catch (e) {
      setError(e);
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className="apartment-management-container fee-status-page">
        <div className="fee-spinner">
          <div></div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="apartment-management-container fee-status-page">
        <div className="fee-status-error">Không tải được dữ liệu: {String(error?.message || error)}</div>
        <div className="fee-collect-actions" style={{ padding: 16 }}>
          <Button className="fee-collect-btn-secondary fee-collect-btn-with-icon" onClick={() => navigate(-1)}>
            Quay lại
          </Button>
        </div>
      </div>
    );
  }

  return (
    <div className="apartment-management-container fee-status-page">
      <div className="fee-status-header">
        <div>
          <div className="fee-status-title">Thu phí</div>
          <div className="fee-status-subtitle">Apartment ID: {id}</div>
        </div>

        <div className="fee-collect-actions fee-status-header-actions">
          <Button className="fee-collect-btn-secondary fee-collect-btn-with-icon" onClick={() => navigate(-1)}>
            Quay lại
          </Button>
          <Button className="fee-collect-btn-secondary fee-collect-btn-with-icon" onClick={selectAll}>
            Chọn tất cả
          </Button>
          <Button className="fee-collect-btn-secondary fee-collect-btn-with-icon" onClick={clearSelection}>
            Bỏ chọn
          </Button>
          <Button
            className="fee-collect-btn-primary fee-collect-btn-with-icon"
            disabled={selectedFeeIds.size === 0 || submitting}
            onClick={onSubmit}
          >
            {submitting ? "Đang thu…" : "Xác nhận thu"}
          </Button>
        </div>
      </div>

      <div className="fee-status-summary" style={{ gridTemplateColumns: "1fr" }}>
        <div className={`fee-status-card fee-status-card--total ${useBalance && canUseBalance ? "is-balance-applied" : ""}`}>
          <div className="fee-status-card-label fee-status-card-label--with-control">
            <span>Tổng đã chọn</span>
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
              <div className="fee-status-total-old">{formatCurrencyVND(selectedTotal)}</div>
              <div className="fee-status-card-value fee-status-card-value--total">{formatCurrencyVND(payableNow)}</div>
              <div className="fee-status-card-sub">
                Dùng số dư: {formatCurrencyVND(appliedFromBalance)} · Số dư còn lại: {formatCurrencyVND(remainingBalanceAfter)}
              </div>
            </div>
          ) : (
            <>
              <div className="fee-status-card-value fee-status-card-value--total">{formatCurrencyVND(selectedTotal)}</div>
              <div className="fee-status-card-sub">{selectedFeeIds.size} khoản</div>
            </>
          )}
        </div>
      </div>

      <div style={{ padding: "0 16px" }}>
        <div className="fee-status-card" style={{ marginTop: 12 }}>
          <div className="fee-status-card-label" style={{ marginBottom: 10 }}>
            Khoản phí chưa thu
          </div>

          {unpaidFees.length === 0 ? (
            <div className="fee-status-empty">Không có khoản phí nào chưa thu.</div>
          ) : (
            <div style={{ display: "flex", flexDirection: "column", gap: 10 }}>
              {unpaidFees.map((fee) => {
                const checked = selectedFeeIds.has(String(fee?.fee_id));
                return (
                  <label
                    key={fee?.fee_id}
                    style={{
                      display: "flex",
                      alignItems: "flex-start",
                      gap: 10,
                      padding: 10,
                      border: "1px solid #d1d5db",
                      borderRadius: 10,
                      background: "#ffffff",
                      cursor: "pointer",
                    }}
                  >
                    <input
                      type="checkbox"
                      checked={checked}
                      onChange={() => toggleFee(fee?.fee_id)}
                      style={{ width: 16, height: 16, marginTop: 2 }}
                    />
                    <div style={{ flex: 1 }}>
                      <div style={{ display: "flex", justifyContent: "space-between", gap: 12 }}>
                        <div style={{ fontWeight: 600 }}>{fee?.fee_name}</div>
                        <div style={{ fontWeight: 600 }}>{formatCurrencyVND(fee?.fee_amount)}</div>
                      </div>
                      <div style={{ opacity: 0.7, fontSize: 13, marginTop: 4 }}>
                        Danh mục: {fee?.fee_category_name || fee?.fee_category_id} · Hết hạn: {fee?.expiry_date || "-"}
                      </div>
                    </div>
                  </label>
                );
              })}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
