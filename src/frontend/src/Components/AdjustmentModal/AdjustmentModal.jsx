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

  const handleChange = (key) => (e) => {
    setForm((prev) => ({ ...prev, [key]: e.target.value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!feeId) return;

    const amount = Number(form.adjustment_amount);
    if (!Number.isFinite(amount)) return;

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
              <input type="date" value={form.effective_date} onChange={handleChange("effective_date")} />
            </div>
            <div className="adjustment-row">
              <label>Ngày hết hạn</label>
              <input type="date" value={form.expiry_date} onChange={handleChange("expiry_date")} />
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
