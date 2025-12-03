import React, { useEffect } from "react";
import "./SuccessModal.css";

export const SuccessModal = ({ isOpen, onClose, message = "Thêm dân cư thành công!", residentData }) => {
  useEffect(() => {
    if (isOpen) {
      const timer = setTimeout(() => {
        onClose();
      }, 3000); // Tự động đóng sau 3 giây

      return () => clearTimeout(timer);
    }
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content success-modal" onClick={(e) => e.stopPropagation()}>
        <div className="success-icon-container">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            className="success-icon"
          >
            <polyline points="20 6 9 17 4 12"></polyline>
          </svg>
        </div>

        <h2 className="success-title">{message}</h2>

        {residentData && (
          <div className="success-details">
            <div className="detail-row">
              <span className="detail-label">Mã căn hộ:</span>
              <span className="detail-value">{residentData.id}</span>
            </div>
            <div className="detail-row">
              <span className="detail-label">Tên cư dân:</span>
              <span className="detail-value">{residentData.name}</span>
            </div>
            <div className="detail-row">
              <span className="detail-label">Số phòng:</span>
              <span className="detail-value">{residentData.room}</span>
            </div>
          </div>
        )}

        <p className="success-footer">Đóng tự động trong 3 giây</p>

        <button
          className="btn-close-success"
          onClick={onClose}
        >
          Đóng
        </button>
      </div>
    </div>
  );
};
