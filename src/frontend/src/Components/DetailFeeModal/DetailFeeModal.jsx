import React from "react";
import "./DetailFeeModal.css";
import Tag from "../Tag/Tag";
import { OBLIGATORY_FEE_TYPE_ID, VOLUNTARY_FEE_TYPE_ID, IMPROMPTU_FEE_TYPE_ID } from "../../constants/feeTypeIds";

const feeTypeMap = {
  [Number(OBLIGATORY_FEE_TYPE_ID)]: { key: "obligatory", label: "Định kỳ" },
  [Number(VOLUNTARY_FEE_TYPE_ID)]: { key: "voluntary", label: "Tự nguyện" },
  [Number(IMPROMPTU_FEE_TYPE_ID)]: { key: "impromptu", label: "Đột xuất" },
};

const statusMap = {
  draft: "Nháp",
  active: "Đang hoạt động",
  closed: "Đã đóng",
  archived: "Lưu trữ",
};

export const DetailFeeModal = ({ isOpen, onClose, fee, loading = false }) => {
  if (!isOpen) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content detail-modal" onClick={(e) => e.stopPropagation()}>
        <div className="detail-header">
          <h2 className="detail-title">Chi tiết khoản phí</h2>
          <button
            className="modal-close"
            onClick={onClose}
            aria-label="Đóng modal"
          >
            ✕
          </button>
        </div>

        <div className="detail-body">
          {loading ? (
            <div style={{ display: 'flex', justifyContent: 'center', padding: 24 }}>
              <div style={{
                border: '4px solid #f3f3f3',
                borderTop: '4px solid #3498db',
                borderRadius: '50%',
                width: '40px',
                height: '40px',
                animation: 'spin 1.5s linear infinite'
              }} />
            </div>
          ) : (
            <div className="detail-card">
            <div className="detail-section">
              <h3 className="section-title">Thông tin cơ bản</h3>

              <div className="detail-grid">
                <div className="detail-item">
                  <label>Mã phí</label>
                  <p className="detail-value">{fee.fee_id}</p>
                </div>

                <div className="detail-item">
                  <label>Tên khoản phí</label>
                  <p className="detail-value">{fee.fee_name || "—"}</p>
                </div>

                <div className="detail-item">
                  <label>Loại phí</label>
                  {feeTypeMap[fee.fee_type_id] ? (
                    <Tag variant="Fee" type={feeTypeMap[fee.fee_type_id].key}>
                      {feeTypeMap[fee.fee_type_id].label}
                    </Tag>
                  ) : (
                    <p className="detail-value">{fee.fee_type_id || "—"}</p>
                  )}
                </div>

                <div className="detail-item">
                  <label>Danh mục</label>
                  <p className="detail-value">{fee.fee_category_id || "—"}</p>
                </div>
              </div>
            </div>

            <div className="detail-section">
              <h3 className="section-title">Chi tiết</h3>

              <div className="detail-grid">
                <div className="detail-item">
                  <label>Số tiền</label>
                  <p className="detail-value">{typeof fee.fee_amount === 'number' ? fee.fee_amount.toLocaleString('vi-VN') + 'đ' : '—'}</p>
                </div>

                <div className="detail-item">
                  <label>Tháng áp dụng</label>
                  <p className="detail-value">{fee.applicable_month || "—"}</p>
                </div>
              </div>
            </div>

            <div className="detail-section">
              <h3 className="section-title">Thời gian áp dụng</h3>

              <div className="detail-grid">
                <div className="detail-item">
                  <label>Ngày hiệu lực</label>
                  <p className="detail-value">{fee.effective_date || "—"}</p>
                </div>

                <div className="detail-item">
                  <label>Ngày hết hạn</label>
                  <p className="detail-value">{fee.expiry_date || "—"}</p>
                </div>
              </div>
            </div>

            <div className="detail-section">
              <h3 className="section-title">Mô tả</h3>
              <div className="detail-grid">
                <div className="detail-item detail-full">
                  <p className="detail-value">{fee.fee_description || "—"}</p>
                </div>
              </div>
            </div>

            <div className="detail-section">
              <h3 className="section-title">Trạng thái</h3>
              <div className="detail-grid">
                <div className="detail-item">
                  <label>Trạng thái</label>
                  {fee.status ? (
                    <Tag variant="Status" status={(fee.status || '').toLowerCase()}>
                      {statusMap[(fee.status || '').toLowerCase()] || fee.status}
                    </Tag>
                  ) : (
                    <p className={`detail-value`}>—</p>
                  )}
                </div>
              </div>
            </div>
            </div>
          )}
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
