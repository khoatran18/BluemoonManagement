import React, { useState } from "react";
import "./AddApartmentForm.css";

export const AddApartmentForm = ({ onSubmit, onCancel }) => {
  const [formData, setFormData] = useState({
    building: "A",
    room_number: "",
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
      building: "A",
      room_number: "",
    });
  };

  return (
    <form className="add-apartment-form" onSubmit={handleSubmit}>
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
            id="room_number"
            name="room_number"
            value={formData.room_number}
            onChange={handleChange}
            placeholder="Nhập số phòng"
            required
          />
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
