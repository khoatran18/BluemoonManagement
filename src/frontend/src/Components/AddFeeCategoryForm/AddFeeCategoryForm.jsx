import React, { useState } from "react";
import "./AddFeeCategoryForm.css";

export default function AddFeeCategoryForm({ onSubmit, onCancel }) {
  const [loading, setLoading] = useState(false);
  const [form, setForm] = useState({
    name: "",
    description: "",
  });

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
