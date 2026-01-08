import React, { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Button from "../../Components/Button/Button";
import LoadingSpinner from "../../Components/LoadingSpinner/LoadingSpinner";
import { useToasts } from "../../Components/Toast/ToastContext";
import {
  getApartmentFeeStatus,
  updateApartmentFeeStatus,
} from "../../api/feeCollectApi";
import { getApartmentDetail } from "../../api/apartmentApi";
import { VOLUNTARY_FEE_TYPE_ID } from "../../constants/feeTypeIds";

import "./FeeCollect.css";

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

function formatVNDNumber(value) {
  const num = Math.max(0, Math.trunc(toNumber(value)));
  try {
    return new Intl.NumberFormat("vi-VN", {
      maximumFractionDigits: 0,
    }).format(num);
  } catch {
    return String(num);
  }
}

function parseVNDNumberInput(value) {
  const digits = String(value ?? "").replace(/[^0-9]/g, "");
  if (!digits) return 0;
  const num = Number(digits);
  return Number.isFinite(num) ? num : 0;
}

function clampNumber(value, min, max) {
  const num = toNumber(value);
  if (!Number.isFinite(num)) return min;
  return Math.min(max, Math.max(min, num));
}

export default function CollectFee() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { addToast } = useToasts();

  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);
  const [data, setData] = useState(null);
  const [apartment, setApartment] = useState(null);
  const [selectedFeeIds, setSelectedFeeIds] = useState(() => new Set());
  const [feePayConfig, setFeePayConfig] = useState(() => ({}));
  const [useBalance, setUseBalance] = useState(false);
  const [prepaidAmount, setPrepaidAmount] = useState(0);
  const [prepaidOpen, setPrepaidOpen] = useState(false);
  const [prepaidDirty, setPrepaidDirty] = useState(false);

  const onNotify = ({ ok, message }) => {
    addToast({
      variant: ok ? "success" : "error",
      message: message || (ok ? "Thu phí thành công." : "Thu phí thất bại."),
      duration: 4000,
    });
  };

  useEffect(() => {
    let cancelled = false;

    async function load() {
      setLoading(true);
      setError(null);

      try {
        // No caching on this screen: always fetch fresh.
        const resp = await getApartmentFeeStatus(id, { useCache: false });
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
  }, [id]);

  useEffect(() => {
    // total_paid is used as prepaid credit (advance payment).
    setPrepaidAmount(toNumber(data?.total_paid));
    setPrepaidDirty(false);
  }, [data?.total_paid]);

  const feeCategoryLabel = (fee) => {
    if (String(fee?.fee_type_id) === VOLUNTARY_FEE_TYPE_ID) return "Phí tự nguyện";
    if (fee?.fee_category_name) return fee.fee_category_name;
    if (fee?.fee_category_id !== undefined && fee?.fee_category_id !== null && String(fee?.fee_category_id) !== "") {
      return `Danh mục ${fee.fee_category_id}`;
    }
    return "Danh mục";
  };

  const groupFeesByCategory = (fees) => {
    const map = new Map();
    for (const fee of fees || []) {
      const isVoluntary = String(fee?.fee_type_id) === VOLUNTARY_FEE_TYPE_ID;
      const categoryId = fee?.fee_category_id;
      const categoryName = fee?.fee_category_name;

      const key = isVoluntary ? "voluntary" : String(categoryId ?? categoryName ?? "unknown");
      const label = isVoluntary ? "Phí tự nguyện" : feeCategoryLabel(fee);

      if (!map.has(key)) {
        map.set(key, { key, label, fees: [] });
      }
      map.get(key).fees.push(fee);
    }

    return Array.from(map.values()).sort((a, b) => a.label.localeCompare(b.label, "vi"));
  };

  useEffect(() => {
    let cancelled = false;

    async function loadApartment() {
      try {
        const detail = await getApartmentDetail(id);
        if (!cancelled) setApartment(detail);
      } catch {
        if (!cancelled) setApartment(null);
      }
    }

    if (id !== undefined && id !== null && String(id).trim() !== "") {
      loadApartment();
    }

    return () => {
      cancelled = true;
    };
  }, [id]);

  const unpaidFees = data?.unpaid_fees || [];
  const voluntaryUnpaidFees = useMemo(
    () => unpaidFees.filter((f) => String(f?.fee_type_id) === VOLUNTARY_FEE_TYPE_ID),
    [unpaidFees]
  );
  const nonVoluntaryUnpaidFees = useMemo(
    () => unpaidFees.filter((f) => String(f?.fee_type_id) !== VOLUNTARY_FEE_TYPE_ID),
    [unpaidFees]
  );
  const balanceCredit = useMemo(() => toNumber(data?.balance), [data?.balance]);
  const prepaidCredit = useMemo(() => toNumber(data?.total_paid), [data?.total_paid]);
  const canUseBalance = balanceCredit > 0;
  const hasFees = unpaidFees.length > 0;
  const hasNonVoluntaryFees = nonVoluntaryUnpaidFees.length > 0;
  const hasSelection = selectedFeeIds.size > 0;

  const getPayAmountForFee = (fee) => {
    const key = String(fee?.fee_id);
    const cfg = feePayConfig?.[key];
    const fullAmount = toNumber(fee?.fee_amount);

    if (String(fee?.fee_type_id) !== VOLUNTARY_FEE_TYPE_ID) return fullAmount;

    if (!cfg) return fullAmount;
    if (!cfg.useCustom) return fullAmount;
    return clampNumber(cfg.amount, 0, fullAmount);
  };

  const selectedFees = useMemo(() => {
    const selected = new Set(Array.from(selectedFeeIds));
    return unpaidFees.filter((f) => selected.has(String(f?.fee_id)));
  }, [unpaidFees, selectedFeeIds]);

  const selectedTotal = useMemo(
    () => selectedFees.reduce((sum, f) => sum + getPayAmountForFee(f), 0),
    [selectedFees, feePayConfig]
  );

  useEffect(() => {
    if (!canUseBalance && useBalance) setUseBalance(false);
  }, [canUseBalance, useBalance]);

  const appliedFromBalance = useMemo(() => {
    if (!useBalance || !canUseBalance) return 0;
    return Math.min(balanceCredit, selectedTotal);
  }, [useBalance, canUseBalance, balanceCredit, selectedTotal]);

  const appliedFromPrepaid = useMemo(() => {
    const afterBalance = Math.max(0, selectedTotal - appliedFromBalance);
    return Math.min(prepaidCredit, afterBalance);
  }, [prepaidCredit, selectedTotal, appliedFromBalance]);

  const payableNow = useMemo(
    () => Math.max(0, selectedTotal - appliedFromBalance - appliedFromPrepaid),
    [selectedTotal, appliedFromBalance, appliedFromPrepaid]
  );

  const remainingPrepaidAfter = useMemo(
    () => Math.max(0, prepaidCredit - appliedFromPrepaid),
    [prepaidCredit, appliedFromPrepaid]
  );
  const remainingBalanceAfter = useMemo(
    () => (useBalance && canUseBalance ? Math.max(0, balanceCredit - appliedFromBalance) : balanceCredit),
    [useBalance, canUseBalance, balanceCredit, appliedFromBalance]
  );

  const groupedNonVoluntaryFees = useMemo(
    () => groupFeesByCategory(nonVoluntaryUnpaidFees),
    [nonVoluntaryUnpaidFees]
  );

  const toggleFee = (fee) => {
    const key = String(fee?.fee_id);
    const fullAmount = toNumber(fee?.fee_amount);
    const isVoluntary = String(fee?.fee_type_id) === VOLUNTARY_FEE_TYPE_ID;

    setSelectedFeeIds((prev) => {
      const next = new Set(prev);
      const willSelect = !next.has(key);
      if (willSelect) next.add(key);
      else next.delete(key);

      setFeePayConfig((prevCfg) => {
        const nextCfg = { ...prevCfg };
        if (willSelect) {
          if (isVoluntary && !nextCfg[key]) nextCfg[key] = { useCustom: false, amount: fullAmount };
        } else {
          delete nextCfg[key];
        }
        return nextCfg;
      });

      return next;
    });
  };

  const selectAll = () => {
    // Apply to non-voluntary fees (fees that need to be paid) only.
    setSelectedFeeIds((prev) => {
      const next = new Set(prev);
      nonVoluntaryUnpaidFees.forEach((f) => next.add(String(f?.fee_id)));
      return next;
    });

    // Keep custom-pay config only for voluntary fees (cleanup legacy keys).
    setFeePayConfig((prevCfg) => {
      const voluntaryKeys = new Set(voluntaryUnpaidFees.map((f) => String(f?.fee_id)));
      const nextCfg = {};
      Object.keys(prevCfg || {}).forEach((k) => {
        if (voluntaryKeys.has(String(k))) nextCfg[k] = prevCfg[k];
      });
      return nextCfg;
    });
  };

  const clearSelection = () => {
    setSelectedFeeIds(new Set());
    setFeePayConfig({});
  };

  const toggleCustomAmount = (fee, checked) => {
    if (String(fee?.fee_type_id) !== VOLUNTARY_FEE_TYPE_ID) return;
    const key = String(fee?.fee_id);
    const fullAmount = toNumber(fee?.fee_amount);
    setFeePayConfig((prevCfg) => ({
      ...prevCfg,
      [key]: {
        useCustom: Boolean(checked),
        amount: clampNumber(prevCfg?.[key]?.amount ?? fullAmount, 0, fullAmount),
      },
    }));
  };

  const updateCustomAmount = (fee, nextRawValue) => {
    if (String(fee?.fee_type_id) !== VOLUNTARY_FEE_TYPE_ID) return;
    const key = String(fee?.fee_id);
    const fullAmount = toNumber(fee?.fee_amount);
    const parsed = parseVNDNumberInput(nextRawValue);
    const clamped = clampNumber(parsed, 0, fullAmount);

    setFeePayConfig((prevCfg) => ({
      ...prevCfg,
      [key]: {
        useCustom: true,
        amount: clamped,
      },
    }));
  };

  const onSubmit = async () => {
    if (submitting) return;
    if (selectedFeeIds.size === 0) return;

    setSubmitting(true);
    setError(null);

    try {
      const existingBalance = toNumber(data?.balance);

      const paid_fees = selectedFees.map((f) => ({
        fee_id: f?.fee_id,
        pay_amount: getPayAmountForFee(f),
      }));

      const unpaid_fees = unpaidFees
        .filter((f) => !selectedFeeIds.has(String(f?.fee_id)))
        .map((f) => ({ fee_id: f?.fee_id }));

      const payload = {
        // Persist prepaid amount exactly as entered.
        balance: useBalance ? remainingBalanceAfter : existingBalance,
        paid_fees,
        unpaid_fees,
      };

      if (prepaidDirty) {
        payload.total_paid = toNumber(prepaidAmount);
      }

      await updateApartmentFeeStatus(id, payload);

      // Keep user on the current screen after confirming.
      // Refresh latest status from server so paid fees disappear from the list.
      const refreshed = await getApartmentFeeStatus(id, { useCache: false });
      setData(refreshed);
      clearSelection();
      setUseBalance(false);
      setPrepaidOpen(false);
      setPrepaidDirty(false);

      onNotify({ ok: true });
    } catch (e) {
      const msg = e?.response?.data?.message || e?.message || String(e);
      onNotify({ ok: false, message: `Thu phí thất bại: ${msg}` });
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className="apartment-management-container fee-status-page">
        <LoadingSpinner />
      </div>
    );
  }

  if (error) {
    return (
      <div className="apartment-management-container fee-status-page">
        <div className="fee-status-error">Không tải được dữ liệu: {String(error?.message || error)}</div>
        <div className="fee-collect-actions" style={{ padding: 16 }}>
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
        </div>
      </div>
    );
  }

  return (
    <div className="apartment-management-container fee-status-page">
      <div className="fee-status-header">
        <div>
          <div className="fee-status-title">Thu phí</div>
          <div className="fee-status-subtitle">
            {apartment?.building && apartment?.room_number
              ? `Căn hộ: ${apartment.building}-${apartment.room_number} · ID: ${id}`
              : `Apartment ID: ${id}`}
          </div>
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
                  d="M9 11l2 2 4-4"
                  stroke="currentColor"
                  strokeWidth="2"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                />
                <path
                  d="M7 4h10a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2Z"
                  stroke="currentColor"
                  strokeWidth="2"
                  strokeLinejoin="round"
                />
              </svg>
            }
            onClick={selectAll}
            disabled={!hasNonVoluntaryFees}
          >
            Chọn tất cả
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
                  d="M15 9l-6 6"
                  stroke="currentColor"
                  strokeWidth="2"
                  strokeLinecap="round"
                />
                <path
                  d="M9 9l6 6"
                  stroke="currentColor"
                  strokeWidth="2"
                  strokeLinecap="round"
                />
                <path
                  d="M12 22a10 10 0 1 0 0-20 10 10 0 0 0 0 20Z"
                  stroke="currentColor"
                  strokeWidth="2"
                />
              </svg>
            }
            onClick={clearSelection}
            disabled={!hasFees || !hasSelection}
          >
            Bỏ chọn
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
                  d="M20 6L9 17l-5-5"
                  stroke="currentColor"
                  strokeWidth="2"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                />
              </svg>
            }
            disabled={!hasSelection || submitting}
            onClick={onSubmit}
          >
            {submitting ? "Đang thu…" : "Xác nhận thu"}
          </Button>
        </div>
      </div>

      <div className="fee-status-top">
        <details
          className="fee-status-accounting-toggle"
          open={prepaidOpen}
          onToggle={(e) => {
            const isOpen = Boolean(e?.currentTarget?.open);
            setPrepaidOpen(isOpen);
            // Reset input to server value when opening/closing unless user is actively updating.
            setPrepaidAmount(prepaidCredit);
            setPrepaidDirty(false);
          }}
        >
          <summary className="fee-status-accounting-toggle-summary">
            {prepaidOpen ? "Ẩn" : "Số tiền trả trước"}
          </summary>
        </details>
      </div>

      {prepaidOpen ? (
        <div className="fee-status-summary" style={{ gridTemplateColumns: "1fr", paddingTop: 0 }}>
          <div className="fee-status-card fee-status-card--prepaid">
            <div className="fee-prepaid-row">
              <div className="fee-prepaid-title">Trả trước</div>
              <div className="fee-pay-amount-input-wrap fee-prepaid-input-wrap">
                <div className={`fee-input-group ${toNumber(prepaidAmount) > 0 ? "is-active" : ""}`}>
                  <input
                    className="fee-pay-amount-input fee-input-group-field fee-prepaid-input-field"
                    inputMode="numeric"
                    value={formatVNDNumber(prepaidAmount)}
                    onChange={(e) => {
                      setPrepaidAmount(parseVNDNumberInput(e?.target?.value));
                      setPrepaidDirty(true);
                    }}
                    placeholder="0"
                    aria-label="Số tiền trả trước"
                  />
                  <span className="fee-input-group-suffix">VND</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      ) : null}

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

          <div className="fee-status-total-values">
            {appliedFromBalance > 0 || appliedFromPrepaid > 0 ? (
              <div className="fee-status-total-old">{formatCurrencyVND(selectedTotal)}</div>
            ) : null}
            <div className="fee-status-card-value fee-status-card-value--total">{formatCurrencyVND(payableNow)}</div>
            <div className="fee-status-card-sub">
              {selectedFeeIds.size} khoản
              {appliedFromBalance > 0 && useBalance && canUseBalance
                ? ` · Dùng số dư: ${formatCurrencyVND(appliedFromBalance)} · Số dư còn lại: ${formatCurrencyVND(remainingBalanceAfter)}`
                : ""}
              {appliedFromPrepaid > 0
                ? ` · Trừ trả trước: ${formatCurrencyVND(appliedFromPrepaid)} · Trả trước còn lại: ${formatCurrencyVND(remainingPrepaidAfter)}`
                : ""}
            </div>
          </div>
        </div>
      </div>

      <div style={{ padding: "0 16px" }}>
        <div className="fee-status-card" style={{ marginTop: 12 }}>
          <div className="fee-status-card-label" style={{ marginBottom: 10 }}>
            Khoản phí cần thu
          </div>

          {nonVoluntaryUnpaidFees.length === 0 ? (
            <div className="fee-status-empty">Không có khoản phí nào chưa thu.</div>
          ) : (
            <div style={{ display: "flex", flexDirection: "column", gap: 10 }}>
              {groupedNonVoluntaryFees.map((group) => {
                const total = group.fees.reduce((sum, fee) => sum + toNumber(fee?.fee_amount), 0);
                return (
                  <details className="fee-status-category" key={group.key}>
                    <summary className="fee-status-category-summary">
                      <span className="fee-status-category-title">{group.label}</span>
                      <span className="fee-status-category-meta">
                        {group.fees.length} khoản · {formatCurrencyVND(total)}
                      </span>
                    </summary>

                    <div className="fee-status-category-body">
                      {group.fees.map((fee) => {
                        const checked = selectedFeeIds.has(String(fee?.fee_id));
                        return (
                          <div key={fee?.fee_id} className={`fee-pay-item ${checked ? "is-selected" : ""}`}>
                            <label
                              className="fee-pay-item-main"
                              style={{
                                display: "flex",
                                alignItems: "flex-start",
                                gap: 10,
                                padding: 10,
                                cursor: "pointer",
                              }}
                            >
                              <input
                                type="checkbox"
                                checked={checked}
                                onChange={() => toggleFee(fee)}
                                style={{ width: 16, height: 16, marginTop: 2 }}
                              />

                              <div style={{ flex: 1, minWidth: 0 }}>
                                <div style={{ display: "flex", justifyContent: "space-between", gap: 12 }}>
                                  <div
                                    style={{
                                      fontWeight: 600,
                                      minWidth: 0,
                                      overflow: "hidden",
                                      textOverflow: "ellipsis",
                                    }}
                                  >
                                    {fee?.fee_name}
                                  </div>
                                  <div style={{ fontWeight: 600, whiteSpace: "nowrap" }}>
                                    {formatCurrencyVND(fee?.fee_amount)}
                                  </div>
                                </div>

                                <div style={{ opacity: 0.7, fontSize: 13, marginTop: 4 }}>
                                  Danh mục: {feeCategoryLabel(fee)} · Hết hạn: {fee?.expiry_date || "-"}
                                </div>
                              </div>
                            </label>
                          </div>
                        );
                      })}
                    </div>
                  </details>
                );
              })}
            </div>
          )}
        </div>

        <div className="fee-status-card" style={{ marginTop: 12 }}>
          <div className="fee-status-card-label" style={{ marginBottom: 10 }}>
            Phí tự nguyện
          </div>

          {voluntaryUnpaidFees.length === 0 ? (
            <div className="fee-status-empty">Không có phí tự nguyện.</div>
          ) : (
            <div style={{ display: "flex", flexDirection: "column", gap: 10 }}>
              {voluntaryUnpaidFees.map((fee) => {
                const checked = selectedFeeIds.has(String(fee?.fee_id));
                const key = String(fee?.fee_id);
                const cfg = feePayConfig?.[key];
                const useCustomAmount = Boolean(cfg?.useCustom);
                const fullAmount = toNumber(fee?.fee_amount);
                const payAmount = checked ? getPayAmountForFee(fee) : fullAmount;

                return (
                  <div key={fee?.fee_id} className={`fee-pay-item ${checked ? "is-selected" : ""}`}>
                    <label
                      className="fee-pay-item-main"
                      style={{
                        display: "flex",
                        alignItems: "flex-start",
                        gap: 10,
                        padding: 10,
                        cursor: "pointer",
                      }}
                    >
                      <input
                        type="checkbox"
                        checked={checked}
                        onChange={() => toggleFee(fee)}
                        style={{ width: 16, height: 16, marginTop: 2 }}
                      />

                      <div style={{ flex: 1, minWidth: 0 }}>
                        <div style={{ display: "flex", justifyContent: "space-between", gap: 12 }}>
                          <div
                            style={{
                              fontWeight: 600,
                              minWidth: 0,
                              overflow: "hidden",
                              textOverflow: "ellipsis",
                            }}
                          >
                            {fee?.fee_name}
                          </div>
                          <div style={{ fontWeight: 600, whiteSpace: "nowrap" }}>
                            {formatCurrencyVND(fee?.fee_amount)}
                          </div>
                        </div>

                        <div style={{ opacity: 0.7, fontSize: 13, marginTop: 4 }}>
                          Danh mục: {feeCategoryLabel(fee)} · Hết hạn: {fee?.expiry_date || "-"}
                        </div>
                      </div>
                    </label>

                    {checked ? (
                      <div className="fee-pay-item-controls">
                        <div className="fee-pay-custom-row">
                          <label className="fee-pay-custom-toggle">
                            <input
                              type="checkbox"
                              checked={useCustomAmount}
                              onChange={(e) => toggleCustomAmount(fee, e?.target?.checked)}
                            />
                            <span>Nhập số tiền</span>
                          </label>

                          <div className="fee-pay-amount-preview">
                            {useCustomAmount ? (
                              <span>
                                Sẽ thu: <strong>{formatCurrencyVND(payAmount)}</strong>
                              </span>
                            ) : (
                              <span>
                                Mặc định: <strong>Toàn bộ</strong>
                              </span>
                            )}
                          </div>
                        </div>

                        {useCustomAmount ? (
                          <div className="fee-pay-amount-row">
                            <div className="fee-pay-amount-input-wrap">
                              <div className={`fee-input-group ${useCustomAmount ? "is-active" : ""}`}>
                                <input
                                  className="fee-pay-amount-input fee-input-group-field"
                                  inputMode="numeric"
                                  placeholder={formatVNDNumber(fullAmount)}
                                  value={formatVNDNumber(cfg?.amount ?? fullAmount)}
                                  onChange={(e) => updateCustomAmount(fee, e?.target?.value)}
                                />
                                <span className="fee-input-group-suffix">₫</span>
                              </div>
                            </div>

                            <div className="fee-pay-amount-hint">Tối đa: {formatCurrencyVND(fullAmount)}</div>
                          </div>
                        ) : null}
                      </div>
                    ) : null}
                  </div>
                );
              })}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
