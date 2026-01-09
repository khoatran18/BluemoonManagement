import React, { useEffect, useMemo, useState } from "react";
import { Modal } from "../Modal/Modal";
import Pagination from "../Pagination/Pagination";
import LoadingSpinner from "../LoadingSpinner/LoadingSpinner";
import { getDeleteApartmentHistories } from "../../api/apartmentApi";
import "./DeleteHistoriesModal.css";

export const DeleteApartmentHistoriesModal = ({ isOpen, onClose, apartmentId }) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [items, setItems] = useState([]);
  const [page, setPage] = useState(1);
  const [limit, setLimit] = useState(10);
  const [totalItems, setTotalItems] = useState(0);

  const title = useMemo(() => {
    const base = "Lịch sử xóa căn hộ";
    if (apartmentId) return `${base} (Căn hộ mã ${apartmentId})`;
    return base;
  }, [apartmentId]);

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
        if (apartmentId) params.apartment_id = Number(apartmentId);

        const resp = await getDeleteApartmentHistories(params);
        const payload = resp?.data ? resp.data : resp;

        setItems(payload?.delete_apartment_histories || []);
        setTotalItems(payload?.total_items || 0);
      } catch (e) {
        setError(e);
        setItems([]);
        setTotalItems(0);
      } finally {
        setLoading(false);
      }
    })();
  }, [isOpen, apartmentId, page, limit]);

  const renderCard = (h) => {
    const titleText = h?.building && h?.room_number ? `${h.building} - ${h.room_number}` : `Apartment #${h?.apartment_id ?? "-"}`;
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
            <span className="delete-history-card__label">Apartment ID</span>
            <span className="delete-history-card__value">{h?.apartment_id ?? "-"}</span>
          </div>
          <div className="delete-history-card__meta-row">
            <span className="delete-history-card__label">Tòa</span>
            <span className="delete-history-card__value">{h?.building || "-"}</span>
          </div>
          <div className="delete-history-card__meta-row">
            <span className="delete-history-card__label">Phòng</span>
            <span className="delete-history-card__value">{h?.room_number || "-"}</span>
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
          Lấy lịch sử xóa căn hộ thất bại
          {"\n"}
          {error?.message || String(error)}
        </div>
      ) : items.length === 0 ? (
        <div className="delete-history-state">Chưa có lịch sử xóa căn hộ.</div>
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
