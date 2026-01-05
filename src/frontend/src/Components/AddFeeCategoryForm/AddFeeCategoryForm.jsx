import React, { useEffect, useState } from "react";
import "./AddFeeCategoryForm.css";

const FEE_TYPE_LABEL_VI = {
  OBLIGATORY: "Định kỳ",
  VOLUNTARY: "Tự nguyện",
  IMPROMPTU: "Đột xuất",
};

const getFeeTypeLabelVi = (feeType) => {
  const raw = String(
    feeType?.code ?? feeType?.key ?? feeType?.name ?? feeType?.fee_type_name ?? ""
  ).trim();
  if (!raw) return "";

  const upper = raw.toUpperCase();
  if (FEE_TYPE_LABEL_VI[upper]) return FEE_TYPE_LABEL_VI[upper];

  const lower = raw.toLowerCase();
  if (lower === "obligatory") return FEE_TYPE_LABEL_VI.OBLIGATORY;
  if (lower === "voluntary") return FEE_TYPE_LABEL_VI.VOLUNTARY;
  if (lower === "impromptu") return FEE_TYPE_LABEL_VI.IMPROMPTU;

  return raw;
};

export default function AddFeeCategoryForm({ feeTypes = [], onSubmit, onCancel }) {
  const [loading, setLoading] = useState(false);
  const [form, setForm] = useState({
    fee_type_id: feeTypes?.[0]?.id || "1",
    name: "",
    description: "",
  });

  useEffect(() => {
    setForm((prev) => ({
      ...prev,
      fee_type_id: prev.fee_type_id || feeTypes?.[0]?.id || "1",
    }));
  }, [feeTypes]);

  const handleChange = (key) => (e) => {
    setForm((prev) => ({ ...prev, [key]: e.target.value }));
  };

  const submit = (e) => {
    e.preventDefault();
    if (!form.name || !String(form.name).trim()) {
      alert("Vui lòng nhập tên danh mục.");
      return;
    }

    const payload = {
      fee_type_id: Number(form.fee_type_id),
      name: String(form.name).trim(),
      description: form.description || "",
    };

    if (typeof onSubmit !== "function") return;

    (async () => {
      try {
        setLoading(true);
        await Promise.resolve(onSubmit(payload));
      } finally {
        setLoading(false);
      }
    })();
  };

  return (
    <form className="add-fee-category-form" onSubmit={submit}>
      <div className="form-group">
        <label htmlFor="fee_type_id">Loại phí</label>
        <select id="fee_type_id" value={form.fee_type_id} onChange={handleChange("fee_type_id")} disabled={loading} required>
          {(feeTypes || []).map((t) => (
            <option key={t.id} value={t.id}>
              {getFeeTypeLabelVi(t)}
            </option>
          ))}
        </select>
      </div>

      <div className="form-group">
        <label htmlFor="name">Tên danh mục</label>
        <input id="name" value={form.name} onChange={handleChange("name")} disabled={loading} required />
      </div>

      <div className="form-group">
        <label htmlFor="description">Mô tả</label>
        <textarea id="description" value={form.description} onChange={handleChange("description")} disabled={loading} />
      </div>

      <div className="form-actions">
        <button type="button" className="btn-cancel" onClick={onCancel} disabled={loading}>
          Hủy
        </button>
        <button type="submit" className="btn-submit" disabled={loading}>
          {loading ? "Đang xử lý..." : "Tạo danh mục"}
        </button>
      </div>
    </form>
  );
}
