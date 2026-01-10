import React, { useEffect, useMemo, useState } from "react";
import { Modal } from "../Modal/Modal";
import Pagination from "../Pagination/Pagination";
import LoadingSpinner from "../LoadingSpinner/LoadingSpinner";
import { getDeleteFeeHistories } from "../../api/feeApi";
import "./DeleteHistoriesModal.css";

function toNumber(value) {
  const num = Number(value);
  return Number.isFinite(num) ? num : 0;
}

function formatCurrencyVND(value) {
  const num = toNumber(value);
  try {
    return new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
      maximumFractionDigits: 0,
    }).format(num);
  } catch {
    return `${num} VND`;
  }
}

export const DeleteFeeHistoriesModal = ({ isOpen, onClose }) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [items, setItems] = useState([]);
  const [page, setPage] = useState(1);
  const [limit, setLimit] = useState(10);
  const [totalItems, setTotalItems] = useState(0);

  const title = useMemo(() => "Lịch sử xóa khoản phí", []);

  useEffect(() => {
    if (!isOpen) return;
    setPage(1);
  }, [isOpen]);

  useEffect(() => {
    if (!isOpen) return;

    (async () => {
      setLoading(true);
      setError(null);
      try {
        const params = { page, limit };
        const resp = await getDeleteFeeHistories(params);
        const payload = resp?.data ? resp.data : resp;

        setItems(payload?.delete_fee_histories || []);
        setTotalItems(payload?.total_items || 0);
      } catch (e) {
        setError(e);
        setItems([]);
        setTotalItems(0);
      } finally {
        setLoading(false);
      }
    })();
  }, [isOpen, page, limit]);

  const renderCard = (h) => {
    const titleText = h?.fee_name || "(Không có tên khoản phí)";
    const right = h?.deleted_at || "-";

    return (
      <div className="delete-history-card" key={h?.history_id ?? Math.random()}>
        <div className="delete-history-card__top">
          <div className="delete-history-card__title" title={titleText}>
            {titleText}
          </div>
          <div className="delete-history-card__value" title={right}>
            {right}
          </div>
        </div>

        <div className="delete-history-card__meta">
          <div className="delete-history-card__meta-row">
            <span className="delete-history-card__label">History ID</span>
            <span className="delete-history-card__value">#{h?.history_id ?? "-"}</span>
          </div>
          <div className="delete-history-card__meta-row">
            <span className="delete-history-card__label">Fee ID</span>
            <span className="delete-history-card__value">{h?.fee_id ?? "-"}</span>
          </div>
          <div className="delete-history-card__meta-row">
            <span className="delete-history-card__label">Loại phí ID</span>
            <span className="delete-history-card__value">{h?.fee_type_id ?? "-"}</span>
          </div>
          <div className="delete-history-card__meta-row">
            <span className="delete-history-card__label">Danh mục ID</span>
            <span className="delete-history-card__value">{h?.fee_category_id ?? "-"}</span>
          </div>
          <div className="delete-history-card__meta-row">
            <span className="delete-history-card__label">Tháng áp dụng</span>
            <span className="delete-history-card__value">{h?.applicable_month || "-"}</span>
          </div>
          <div className="delete-history-card__meta-row">
            <span className="delete-history-card__label">Số tiền</span>
            <span className="delete-history-card__value">{formatCurrencyVND(h?.amount)}</span>
          </div>
          <div className="delete-history-card__meta-row">
            <span className="delete-history-card__label">Bắt đầu</span>
            <span className="delete-history-card__value">{h?.start_date || "-"}</span>
          </div>
          <div className="delete-history-card__meta-row">
            <span className="delete-history-card__label">Kết thúc</span>
            <span className="delete-history-card__value">{h?.end_date || "-"}</span>
          </div>
          <div className="delete-history-card__meta-row">
            <span className="delete-history-card__label">Trạng thái</span>
            <span className="delete-history-card__value">{h?.status || "-"}</span>
          </div>
        </div>
      </div>
    );
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={title}>
      {loading ? (
        <LoadingSpinner />
      ) : error ? (
        <div className="delete-history-state delete-history-state--error">
          Lấy lịch sử xóa khoản phí thất bại
          {"\n"}
          {error?.message || String(error)}
        </div>
      ) : items.length === 0 ? (
        <div className="delete-history-state">Chưa có lịch sử xóa khoản phí.</div>
      ) : (
        <>
          <div className="delete-history-list">{items.map(renderCard)}</div>
          <Pagination
            total={totalItems}
            page={page}
            limit={limit}
            onPageChange={setPage}
            onLimitChange={(l) => {
              setLimit(l);
              setPage(1);
            }}
          />
        </>
      )}
    </Modal>
  );
};
