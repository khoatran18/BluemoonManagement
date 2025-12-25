import React, { useState, useEffect } from "react";
import "./EditApartmentModal.css";

export const EditApartmentModal = ({ isOpen, onClose, apartment, onSubmit }) => {
  const [formData, setFormData] = useState({
    id: "",
    building: "A",
    room: "",
    area: "",
    status: "Trống",
  });

  useEffect(() => {
    if (apartment) {
      setFormData({
        id: apartment.id || "",
        building: apartment.building || "A",
        room: apartment.room || "",
        area: apartment.area || "",
        status: apartment.status || "Trống",
      });
    }
  }, [apartment, isOpen]);

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
    onClose();
  };

  if (!isOpen || !apartment) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content edit-apartment-modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2 className="modal-title">Chỉnh sửa thông tin căn hộ</h2>
          <button
            className="modal-close"
            onClick={onClose}
            aria-label="Đóng modal"
          >
            ✕
          </button>
        </div>

        <form className="edit-form" onSubmit={handleSubmit}>
          <div className="form-body">
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
                disabled
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
          </div>

          <div className="form-footer">
            <button
              type="button"
              className="btn-cancel"
              onClick={onClose}
            >
              Hủy
            </button>
            <button
              type="submit"
              className="btn-submit"
            >
              Cập nhật
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
