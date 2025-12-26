import React, { useState, useEffect } from "react";
import "./AddFeeForm.css";

export const AddFeeForm = ({ initial = {}, feeTypes = [], feeCategories = [], onSubmit, onCancel, isEditing = false }) => {
  const [formData, setFormData] = useState({
    fee_type_id: initial.fee_type_id || (feeTypes[0] && feeTypes[0].id) || "",
    fee_category_id: initial.fee_category_id || (feeCategories[0] && feeCategories[0].fee_category_id) || "",
    fee_name: initial.fee_name || "",
    fee_description: initial.fee_description || "",
    fee_amount: initial.fee_amount || "",
    applicable_month: initial.applicable_month || "",
    effective_date: initial.effective_date || "",
    expiry_date: initial.expiry_date || "",
    status: initial.status || "active",
  });

  useEffect(() => {
    setFormData({
      fee_type_id: initial.fee_type_id || (feeTypes[0] && feeTypes[0].id) || "",
      fee_category_id: initial.fee_category_id || (feeCategories[0] && feeCategories[0].fee_category_id) || "",
      fee_name: initial.fee_name || "",
      fee_description: initial.fee_description || "",
      fee_amount: initial.fee_amount || "",
      applicable_month: initial.applicable_month || "",
      effective_date: initial.effective_date || "",
      expiry_date: initial.expiry_date || "",
      status: initial.status || "active",
    });
  }, [initial, feeTypes, feeCategories]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const submit = (e) => {
    e.preventDefault();
    if (onSubmit) onSubmit(formData);
  };

  return (
    <form className="add-fee-form" onSubmit={submit}>
      <div className="form-group">
        <label htmlFor="fee_type_id">Loại phí</label>
        <select id="fee_type_id" name="fee_type_id" value={formData.fee_type_id} onChange={handleChange} disabled={isEditing} required>
          {feeTypes.map(ft => (
            <option key={ft.id} value={ft.id}>{ft.name}</option>
          ))}
        </select>
      </div>

      <div className="form-group">
        <label htmlFor="fee_category_id">Danh mục</label>
        <select id="fee_category_id" name="fee_category_id" value={formData.fee_category_id} onChange={handleChange} disabled={isEditing} required>
          {feeCategories.map(fc => (
            <option key={fc.fee_category_id} value={fc.fee_category_id}>{fc.name}</option>
          ))}
        </select>
      </div>

      <div className="form-group">
        <label htmlFor="fee_name">Tên khoản phí</label>
        <input id="fee_name" name="fee_name" value={formData.fee_name} onChange={handleChange} required />
      </div>

      <div className="form-group">
        <label htmlFor="fee_amount">Số tiền</label>
        <input id="fee_amount" name="fee_amount" type="number" value={formData.fee_amount} onChange={handleChange} required />
      </div>

      <div className="form-group">
        <label htmlFor="applicable_month">Tháng áp dụng</label>
        <input id="applicable_month" name="applicable_month" type="month" value={formData.applicable_month} onChange={handleChange} />
      </div>

      <div className="form-group">
        <label htmlFor="effective_date">Ngày hiệu lực</label>
        <input id="effective_date" name="effective_date" type="date" value={formData.effective_date} onChange={handleChange} />
      </div>

      <div className="form-group">
        <label htmlFor="expiry_date">Ngày hết hạn</label>
        <input id="expiry_date" name="expiry_date" type="date" value={formData.expiry_date} onChange={handleChange} />
      </div>

      <div className="form-group">
        <label htmlFor="fee_description">Mô tả</label>
        <textarea id="fee_description" name="fee_description" value={formData.fee_description} onChange={handleChange} />
      </div>

      <div className="form-group">
        <label htmlFor="status">Trạng thái</label>
        <select id="status" name="status" value={formData.status} onChange={handleChange} required>
          <option value="active">Đang hoạt động</option>
          <option value="draft">Nháp</option>
          <option value="closed">Đã đóng</option>
          <option value="archived">Lưu trữ</option>
        </select>
      </div>

      <div className="form-actions">
        <button type="button" className="btn-cancel" onClick={onCancel}>Hủy</button>
        <button type="submit" className="btn-submit">{isEditing ? 'Cập nhật' : 'Tạo mới'}</button>
      </div>
    </form>
  );
};

export default AddFeeForm;
