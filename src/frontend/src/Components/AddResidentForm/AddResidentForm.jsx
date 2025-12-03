import React, { useState } from "react";
import "./AddResidentForm.css";

export const AddResidentForm = ({ onSubmit, onCancel }) => {
  const [formData, setFormData] = useState({
    id: "",
    name: "",
    room: "",
    building: "",
    email: "",
    phone: "",
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (onSubmit) {
      onSubmit(formData);
    }
    setFormData({
      id: "",
      name: "",
      room: "",
      building: "",
      email: "",
      phone: "",
    });
  };

  return (
    <form className="add-resident-form" onSubmit={handleSubmit}>
      <div className="form-group">
        <label htmlFor="id">Mã căn hộ</label>
        <input
          type="text"
          id="id"
          name="id"
          value={formData.id}
          onChange={handleChange}
          placeholder="Nhập mã căn hộ"
          required
        />
      </div>

      <div className="form-group">
        <label htmlFor="name">Tên cư dân</label>
        <input
          type="text"
          id="name"
          name="name"
          value={formData.name}
          onChange={handleChange}
          placeholder="Nhập tên cư dân"
          required
        />
      </div>

      <div className="form-row">
        <div className="form-group">
          <label htmlFor="room">Số phòng</label>
          <input
            type="text"
            id="room"
            name="room"
            value={formData.room}
            onChange={handleChange}
            placeholder="Nhập số phòng"
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="building">Tòa nhà</label>
          <input
            type="text"
            id="building"
            name="building"
            value={formData.building}
            onChange={handleChange}
            placeholder="Nhập tòa nhà"
            required
          />
        </div>
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
        />
      </div>

      <div className="form-group">
        <label htmlFor="phone">Số điện thoại</label>
        <input
          type="tel"
          id="phone"
          name="phone"
          value={formData.phone}
          onChange={handleChange}
          placeholder="Nhập số điện thoại"
        />
      </div>

      <div className="form-actions">
        <button
          type="button"
          className="btn-cancel"
          onClick={onCancel}
        >
          Hủy
        </button>
        <button
          type="submit"
          className="btn-submit"
        >
          Thêm cư dân
        </button>
      </div>
    </form>
  );
};
