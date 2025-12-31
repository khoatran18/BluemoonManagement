import React from "react";
import "./DeleteConfirmModal.css";

export const DeleteConfirmModal = ({ isOpen, onClose, data, onConfirm, title }) => {
  if (!isOpen || !data) return null;

  const displayName =
    data?.name ||
    data?.full_name ||
    (data?.building && (data?.room || data?.room_number)
      ? `${data.building} - ${data.room || data.room_number}`
      : data?.room || data?.room_number || "");

  const handleConfirm = () => {
    if (onConfirm) {
      onConfirm(data.id);
    }
    if (onClose) onClose();
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content delete-modal" onClick={(e) => e.stopPropagation()}>
        <div className="delete-icon-container">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            className="delete-icon"
          >
            <path d="M3 6h18M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2m3 0v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6h14zM10 11v6M14 11v6" />
          </svg>
        </div>

        <h2 className="delete-title">{title ? `Xóa ${title}` : "Xóa mục"}</h2>
        
        <p className="delete-message">
          Bạn có chắc chắn muốn xóa {title ? title : "mục"} {displayName ? <strong>{displayName}</strong> : null} (Mã: {data.id}) không?
        </p>

        <p className="delete-warning">
          Hành động này không thể hoàn tác
        </p>

        <div className="delete-actions">
          <button
            className="btn-cancel-delete"
            onClick={onClose}
          >
            Hủy
          </button>
          <button
            className="btn-confirm-delete"
            onClick={handleConfirm}
          >
            Xóa
          </button>
        </div>
      </div>
    </div>
  );
};
