import React from "react";
import "./FeeAdjustmentsModal.css";

function formatVND(value) {
  const num = Number(value || 0);
  if (!Number.isFinite(num)) return "0đ";
  return `${num.toLocaleString("vi-VN")}đ`;
}

export const FeeAdjustmentsModal = ({
  isOpen,
  onClose,
  fee,
  adjustments,
  loading,
  onAdd,
  onEdit,
  onDelete,
}) => {
  if (!isOpen) return null;

  const list = Array.isArray(adjustments) ? adjustments : [];
  const isClosed = String(fee?.status || "").toLowerCase() === "closed";

  return (
    <div className="modal-overlay fee-adjustments-overlay" onClick={onClose}>
      <div className="modal-content fee-adjustments-modal" onClick={(e) => e.stopPropagation()}>
        <div className="fee-adjustments-modal__header">
          <div>
            <h2 className="fee-adjustments-modal__title">Điều chỉnh của khoản phí</h2>
            <div className="fee-adjustments-modal__subtitle">
              {fee?.fee_name ? fee.fee_name : "—"} {fee?.fee_id ? `(Mã: ${fee.fee_id})` : ""}
            </div>
          </div>
          <button className="modal-close" onClick={onClose} aria-label="Đóng modal">
            ✕
          </button>
        </div>

        <div className="fee-adjustments-modal__body">
          <div className="fee-adjustments-modal__actions">
            <button
              type="button"
              className="fee-adjustments-modal__add"
              onClick={() => {
                if (isClosed) return;
                onAdd?.();
              }}
              disabled={isClosed}
              aria-disabled={isClosed}
              title={isClosed ? "Phí này đã đóng" : undefined}
            >
              Thêm điều chỉnh
            </button>
          </div>

          {isClosed ? <div className="fee-adjustments-modal__closedNote">Phí này đã đóng</div> : null}

          {loading ? (
            <div className="fee-adjustments-modal__loading">Đang tải...</div>
          ) : list.length === 0 ? (
            <div className="fee-adjustments-modal__empty">Chưa có điều chỉnh</div>
          ) : (
            <div className="fee-adjustments-modal__list">
              {list.map((adj) => {
                const type = String(adj?.adjustment_type || "").toLowerCase();
                const isDecrease = type.includes("decrease");
                const signed = `${isDecrease ? "-" : "+"}${formatVND(adj?.adjustment_amount)}`;

                return (
                  <div className="fee-adjustments-modal__item" key={adj?.adjustment_id}>
                    <div className="fee-adjustments-modal__itemMain">
                      <div className="fee-adjustments-modal__itemTitle">
                        #{adj?.adjustment_id} · {adj?.reason || "(Không có lý do)"}
                      </div>
                      <div className="fee-adjustments-modal__itemMeta">
                        {adj?.effective_date || "—"} → {adj?.expiry_date || "—"}
                      </div>
                    </div>

                    <div className="fee-adjustments-modal__itemSide">
                      <div className="fee-adjustments-modal__amount">{signed}</div>
                      <div className="fee-adjustments-modal__itemButtons">
                        <button
                          type="button"
                          onClick={() => {
                            if (isClosed) return;
                            onEdit?.(adj);
                          }}
                          disabled={isClosed}
                          aria-disabled={isClosed}
                          title={isClosed ? "Phí này đã đóng" : undefined}
                        >
                          Sửa
                        </button>
                        <button
                          type="button"
                          onClick={() => {
                            if (isClosed) return;
                            onDelete?.(adj);
                          }}
                          disabled={isClosed}
                          aria-disabled={isClosed}
                          title={isClosed ? "Phí này đã đóng" : undefined}
                        >
                          Xóa
                        </button>
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>

        <div className="fee-adjustments-modal__footer">
          <button type="button" className="fee-adjustments-modal__close" onClick={onClose}>
            Đóng
          </button>
        </div>
      </div>
    </div>
  );
};
