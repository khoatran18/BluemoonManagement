import React, { useState, useEffect } from "react";
import "./EditResidentModal.css";

export const EditResidentModal = ({ isOpen, onClose, resident, onSubmit }) => {
  const [formData, setFormData] = useState({
    id: "",
    name: "",
    room: "",
    building: "",
    email: "",
    phone: "",
  });

  useEffect(() => {
    if (resident) {
      setFormData({
        id: resident.id || "",
        name: resident.name || "",
        room: resident.room || "",
        building: resident.building || "A",
        email: resident.email || "",
        phone: resident.phone || "",
      });
    }
  }, [resident, isOpen]);

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

  if (!isOpen || !resident) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content edit-modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2 className="modal-title">Chỉnh sửa thông tin cư dân</h2>
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
