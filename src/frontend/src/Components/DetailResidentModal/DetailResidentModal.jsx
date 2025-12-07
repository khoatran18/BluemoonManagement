import React from "react";
import "./DetailResidentModal.css";

export const DetailResidentModal = ({ isOpen, onClose, resident }) => {
  if (!isOpen || !resident) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content detail-modal" onClick={(e) => e.stopPropagation()}>
        <div className="detail-header">
          <h2 className="detail-title">Chi tiết cư dân</h2>
          <button
            className="modal-close"
            onClick={onClose}
            aria-label="Đóng modal"
          >
            ✕
          </button>
        </div>

        <div className="detail-body">
          <div className="detail-card">
            <div className="detail-section">
              <h3 className="section-title">Thông tin cơ bản</h3>
              
              <div className="detail-grid">
                <div className="detail-item">
                  <label>Mã căn hộ</label>
                  <p className="detail-value">{resident.id}</p>
                </div>

                <div className="detail-item">
                  <label>Tên cư dân</label>
                  <p className="detail-value">{resident.name}</p>
                </div>

                <div className="detail-item">
                  <label>Số phòng</label>
                  <p className="detail-value">{resident.room}</p>
                </div>

                <div className="detail-item">
                  <label>Tòa nhà</label>
                  <p className="detail-value">A</p>
                </div>
              </div>
            </div>

            <div className="detail-section">
              <h3 className="section-title">Thông tin liên hệ</h3>
              
              <div className="detail-grid">
                <div className="detail-item">
                  <label>Email</label>
                  <p className="detail-value">—</p>
                </div>

                <div className="detail-item">
                  <label>Số điện thoại</label>
                  <p className="detail-value">—</p>
                </div>
              </div>
            </div>

            <div className="detail-section">
              <h3 className="section-title">Thông tin khác</h3>
              
              <div className="detail-grid">
                <div className="detail-item">
                  <label>Trạng thái</label>
                  <p className="detail-value status-active">Hoạt động</p>
                </div>

                <div className="detail-item">
                  <label>Ngày tạo</label>
                  <p className="detail-value">04/12/2025</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div className="detail-footer">
          <button
            className="btn-close-detail"
            onClick={onClose}
          >
            Đóng
          </button>
        </div>
      </div>
    </div>
  );
};
