import React, { useState } from "react";
import "./AddApartmentForm.css";

export const AddApartmentForm = ({ onSubmit, onCancel }) => {
  const [formData, setFormData] = useState({
    id: "",
    building: "A",
    room: "",
    area: "",
    status: "Trống",
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
      building: "A",
      room: "",
      area: "",
      status: "Trống",
    });
  };

  return (
    <form className="add-apartment-form" onSubmit={handleSubmit}>
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

      <div className="form-row">
        <div className="form-group">
          <label htmlFor="building">Tòa nhà</label>
          <select
            id="building"
            name="building"
            value={formData.building}
            onChange={handleChange}
            required
          >
            <option value="A">A</option>
            <option value="B">B</option>
            <option value="C">C</option>
          </select>
        </div>

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
      </div>

      <div className="form-row">
        <div className="form-group">
          <label htmlFor="area">Diện tích</label>
          <input
            type="text"
            id="area"
            name="area"
            value={formData.area}
            onChange={handleChange}
            placeholder="Nhập diện tích (m²)"
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="status">Trạng thái</label>
          <select
            id="status"
            name="status"
            value={formData.status}
            onChange={handleChange}
            required
          >
            <option value="Trống">Trống</option>
            <option value="Có dân cư">Có dân cư</option>
          </select>
        </div>
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
          Thêm căn hộ
        </button>
      </div>
    </form>
  );
};
