import React from "react";
import "./DetailResidentModal.css";

export const DetailResidentModal = ({ isOpen, onClose, resident, loading = false }) => {
  if (!isOpen) return null;

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
            {loading ? (
              <div className="detail-section">
                <h3 className="section-title">Đang tải</h3>
                <p className="detail-value">Đang tải dữ liệu cư dân...</p>
              </div>
            ) : !resident ? (
              <div className="detail-section">
                <h3 className="section-title">Không có dữ liệu</h3>
                <p className="detail-value">Không lấy được thông tin cư dân.</p>
              </div>
            ) : (
              <>
                <div className="detail-section">
                  <h3 className="section-title">Thông tin cơ bản</h3>

                  <div className="detail-grid">
                    <div className="detail-item">
                      <label>Mã cư dân</label>
                      <p className="detail-value">{resident.id}</p>
                    </div>

                    <div className="detail-item">
                      <label>Tên cư dân</label>
                      <p className="detail-value">{resident.full_name || resident.name || "—"}</p>
                    </div>

                    <div className="detail-item">
                      <label>Số phòng</label>
                      <p className="detail-value">{resident.room || resident.apartment?.room_number || "—"}</p>
                    </div>

                    <div className="detail-item">
                      <label>Tòa nhà</label>
                      <p className="detail-value">{resident.building || resident.apartment?.building || "—"}</p>
                    </div>
                  </div>
                </div>

                <div className="detail-section">
                  <h3 className="section-title">Thông tin liên hệ</h3>

                  <div className="detail-grid">
                    <div className="detail-item">
                      <label>Email</label>
                      <p className="detail-value">{resident.email || "—"}</p>
                    </div>

                    <div className="detail-item">
                      <label>Số điện thoại</label>
                      <p className="detail-value">{resident.phone_number || resident.phone || "—"}</p>
                    </div>
                  </div>
                </div>

                <div className="detail-section">
                  <h3 className="section-title">Thông tin khác</h3>

                  <div className="detail-grid">
                    <div className="detail-item">
                      <label>Chủ hộ</label>
                      <p className="detail-value">{resident.is_head ? "Có" : "Không"}</p>
                    </div>
                  </div>
                </div>
              </>
            )}
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
