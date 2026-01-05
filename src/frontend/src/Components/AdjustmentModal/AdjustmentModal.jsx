import React, { useEffect, useMemo, useState } from "react";
import "./AdjustmentModal.css";

const TYPE_OPTIONS = [
  { value: "decrease", label: "Giảm" },
  { value: "increase", label: "Tăng" },
];

export const AdjustmentModal = ({
  isOpen,
  onClose,
  feeId,
  feeEffectiveDate,
  feeExpiryDate,
  initialAdjustment,
  onSubmit,
  loading = false,
}) => {
  const isEdit = useMemo(() => !!initialAdjustment?.adjustment_id, [initialAdjustment]);

  const [form, setForm] = useState({
    adjustment_amount: "",
    adjustment_type: "decrease",
    reason: "",
    effective_date: "",
    expiry_date: "",
  });

  useEffect(() => {
    if (!isOpen) return;
    setForm({
      adjustment_amount:
        initialAdjustment?.adjustment_amount !== undefined && initialAdjustment?.adjustment_amount !== null
          ? String(initialAdjustment.adjustment_amount)
          : "",
      adjustment_type: initialAdjustment?.adjustment_type ? String(initialAdjustment.adjustment_type).toLowerCase() : "decrease",
      reason: initialAdjustment?.reason || "",
      effective_date: initialAdjustment?.effective_date || "",
      expiry_date: initialAdjustment?.expiry_date || "",
    });
  }, [isOpen, initialAdjustment]);

  if (!isOpen) return null;

  const normalizeDate = (d) => {
    const s = String(d || "").trim();
    return s ? s : "";
  };

  const inRange = (date, min, max) => {
    const v = normalizeDate(date);
    if (!v) return true;
    const minV = normalizeDate(min);
    const maxV = normalizeDate(max);
    if (minV && v < minV) return false;
    if (maxV && v > maxV) return false;
    return true;
  };

  const maxDate = (a, b) => {
    const A = normalizeDate(a);
    const B = normalizeDate(b);
    if (!A) return B;
    if (!B) return A;
    return A > B ? A : B;
  };

  const handleChange = (key) => (e) => {
    const value = e.target.value;
    if (key === "effective_date") {
      setForm((prev) => {
        const next = { ...prev, effective_date: value };
        if (next.expiry_date && value && next.expiry_date < value) {
          next.expiry_date = "";
        }
        return next;
      });
      return;
    }
    setForm((prev) => ({ ...prev, [key]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!feeId) return;

    const amount = Number(form.adjustment_amount);
    if (!Number.isFinite(amount)) return;

    // Date constraints: adjustment dates must stay within the parent fee's date range.
    if (!inRange(form.effective_date, feeEffectiveDate, feeExpiryDate)) {
      alert("Ngày hiệu lực của điều chỉnh phải nằm trong khoảng ngày hiệu lực/ngày hết hạn của khoản phí.");
      return;
    }
    if (!inRange(form.expiry_date, feeEffectiveDate, feeExpiryDate)) {
      alert("Ngày hết hạn của điều chỉnh phải nằm trong khoảng ngày hiệu lực/ngày hết hạn của khoản phí.");
      return;
    }
    if (form.effective_date && form.expiry_date && form.expiry_date < form.effective_date) {
      alert("Ngày hết hạn phải sau hoặc bằng Ngày hiệu lực.");
      return;
    }

    const payload = {
      fee_id: feeId,
      adjustment_amount: amount,
      adjustment_type: form.adjustment_type,
      reason: form.reason,
      effective_date: form.effective_date || null,
      expiry_date: form.expiry_date || null,
    };

    if (typeof onSubmit === "function") {
      await onSubmit(payload);
    }
  };

  return (
    <div className="modal-overlay adjustment-overlay" onClick={onClose}>
      <div className="modal-content adjustment-modal" onClick={(e) => e.stopPropagation()}>
        <div className="adjustment-header">
          <h2 className="adjustment-title">{isEdit ? "Sửa điều chỉnh" : "Thêm điều chỉnh"}</h2>
          <button className="modal-close" onClick={onClose} aria-label="Đóng modal">
            ✕
          </button>
        </div>

        <form className="adjustment-body" onSubmit={handleSubmit}>
          <div className="adjustment-row">
            <label>Loại điều chỉnh</label>
            <select value={form.adjustment_type} onChange={handleChange("adjustment_type")}>
              {TYPE_OPTIONS.map((o) => (
                <option key={o.value} value={o.value}>
                  {o.label}
                </option>
              ))}
            </select>
          </div>

          <div className="adjustment-row">
            <label>Số tiền</label>
            <input
              type="number"
              inputMode="numeric"
              value={form.adjustment_amount}
              onChange={handleChange("adjustment_amount")}
              placeholder="50000"
              required
            />
          </div>

          <div className="adjustment-row">
            <label>Lý do</label>
            <input type="text" value={form.reason} onChange={handleChange("reason")} placeholder="Ưu đãi/Phụ phí..." />
          </div>

          <div className="adjustment-grid">
            <div className="adjustment-row">
              <label>Ngày hiệu lực</label>
              <input
                type="date"
                value={form.effective_date}
                onChange={handleChange("effective_date")}
                min={normalizeDate(feeEffectiveDate) || undefined}
                max={normalizeDate(feeExpiryDate) || undefined}
              />
            </div>
            <div className="adjustment-row">
              <label>Ngày hết hạn</label>
              <input
                type="date"
                value={form.expiry_date}
                onChange={handleChange("expiry_date")}
                min={maxDate(feeEffectiveDate, form.effective_date) || undefined}
                max={normalizeDate(feeExpiryDate) || undefined}
              />
            </div>
          </div>

          <div className="adjustment-footer">
            <button type="button" className="btn-cancel" onClick={onClose} disabled={loading}>
              Hủy
            </button>
            <button type="submit" className="btn-submit" disabled={loading}>
              {loading ? "Đang lưu..." : "Lưu"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
