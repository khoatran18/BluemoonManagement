import React, { useEffect, useMemo, useState } from "react";
import { Modal } from "../Modal/Modal";
import Pagination from "../Pagination/Pagination";
import LoadingSpinner from "../LoadingSpinner/LoadingSpinner";
import { getDeleteResidentHistories } from "../../api/residentApi";
import "./DeleteHistoriesModal.css";

export const DeleteResidentHistoriesModal = ({
  isOpen,
  onClose,
  apartmentId,
  residentId,
}) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [items, setItems] = useState([]);
  const [page, setPage] = useState(1);
  const [limit, setLimit] = useState(10);
  const [totalItems, setTotalItems] = useState(0);

  const title = useMemo(() => {
    const base = "Lịch sử xóa cư dân";
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
        const params = {
          page,
          limit,
        };

        if (apartmentId) params.apartment_id = Number(apartmentId);
        if (residentId) params.resident_id = Number(residentId);

        const resp = await getDeleteResidentHistories(params);
        const payload = resp?.data ? resp.data : resp;

        setItems(payload?.delete_resident_histories || []);
        setTotalItems(payload?.total_items || 0);
      } catch (e) {
        setError(e);
        setItems([]);
        setTotalItems(0);
      } finally {
        setLoading(false);
      }
    })();
  }, [isOpen, apartmentId, residentId, page, limit]);

  const renderCard = (h) => {
    const titleText = h?.full_name || "(Không có tên)";
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
            <span className="delete-history-card__label">Resident ID</span>
            <span className="delete-history-card__value">{h?.resident_id ?? "-"}</span>
          </div>
          <div className="delete-history-card__meta-row">
            <span className="delete-history-card__label">Apartment ID</span>
            <span className="delete-history-card__value">{h?.apartment_id ?? "-"}</span>
          </div>
          <div className="delete-history-card__meta-row">
            <span className="delete-history-card__label">SĐT</span>
            <span className="delete-history-card__value">{h?.phone_number || "-"}</span>
          </div>
          <div className="delete-history-card__meta-row">
            <span className="delete-history-card__label">Email</span>
            <span className="delete-history-card__value">{h?.email || "-"}</span>
          </div>
          <div className="delete-history-card__meta-row">
            <span className="delete-history-card__label">Chủ hộ</span>
            <span className="delete-history-card__value">{h?.is_head ? "Có" : "Không"}</span>
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
          Lấy lịch sử xóa cư dân thất bại
          {"\n"}
          {error?.message || String(error)}
        </div>
      ) : items.length === 0 ? (
        <div className="delete-history-state">Chưa có lịch sử xóa cư dân.</div>
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
