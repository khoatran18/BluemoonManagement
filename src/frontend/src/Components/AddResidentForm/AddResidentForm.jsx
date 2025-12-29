import React, { useState } from "react";
import "./AddResidentForm.css";

export const AddResidentForm = ({ onSubmit, onCancel }) => {
  const [formData, setFormData] = useState({
    apartment_id: "",
    full_name: "",
    email: "",
    phone_number: "",
  });

  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
    // Clear error for this field
    if (errors[name]) {
      setErrors((prev) => ({
        ...prev,
        [name]: "",
      }));
    }
  };

  const validateForm = () => {
    const newErrors = {};
    if (!formData.apartment_id.trim()) {
      newErrors.apartment_id = "Mã căn hộ không được bỏ trống";
    }
    if (!formData.full_name.trim()) {
      newErrors.full_name = "Tên cư dân không được bỏ trống";
    }
    if (formData.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      newErrors.email = "Email không hợp lệ";
    }
    return newErrors;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const newErrors = validateForm();
    
    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    setIsSubmitting(true);
    try {
      if (onSubmit) {
        await onSubmit(formData);
      }
      setFormData({
        apartment_id: "",
        full_name: "",
        email: "",
        phone_number: "",
      });
      setErrors({});
    } catch (error) {
      console.error("Error submitting form:", error);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <form className="add-resident-form" onSubmit={handleSubmit}>
      <div className="form-group">
        <label htmlFor="apartment_id">Mã căn hộ</label>
        <input
          type="text"
          id="apartment_id"
          name="apartment_id"
          value={formData.apartment_id}
          onChange={handleChange}
          placeholder="Nhập mã căn hộ"
          disabled={isSubmitting}
        />
        {errors.apartment_id && <span className="error-text">{errors.apartment_id}</span>}
      </div>

      <div className="form-group">
        <label htmlFor="full_name">Tên cư dân</label>
        <input
          type="text"
          id="full_name"
          name="full_name"
          value={formData.full_name}
          onChange={handleChange}
          placeholder="Nhập tên cư dân"
          disabled={isSubmitting}
        />
        {errors.full_name && <span className="error-text">{errors.full_name}</span>}
      </div>

      <div className="form-group">
        <label htmlFor="email">Email</label>
        <input
          type="email"
          id="email"
          name="email"
          value={formData.email}
          onChange={handleChange}
          placeholder="Nhập email"
          disabled={isSubmitting}
        />
        {errors.email && <span className="error-text">{errors.email}</span>}
      </div>

      <div className="form-group">
        <label htmlFor="phone_number">Số điện thoại</label>
        <input
          type="tel"
          id="phone_number"
          name="phone_number"
          value={formData.phone_number}
          onChange={handleChange}
          placeholder="Nhập số điện thoại"
          disabled={isSubmitting}
        />
      </div>

      <div className="form-actions">
        <button
          type="button"
          className="btn-cancel"
          onClick={onCancel}
          disabled={isSubmitting}
        >
          Hủy
        </button>
        <button
          type="submit"
          className="btn-submit"
          disabled={isSubmitting}
        >
          {isSubmitting ? "Đang thêm..." : "Thêm cư dân"}
        </button>
      </div>
    </form>
  );
};
