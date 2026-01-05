import React, { useEffect, useMemo, useState } from "react";
import { Modal } from "../Modal/Modal";
import Pagination from "../Pagination/Pagination";
import { getPayHistories } from "../../api/payHistoryApi";
import Tag from "../Tag/Tag";
import "./PaymentHistoriesModal.css";
import LoadingSpinner from "../LoadingSpinner/LoadingSpinner";

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

const feeTypeTagMap = {
  OBLIGATORY: { type: "obligatory", label: "Định kỳ" },
  VOLUNTARY: { type: "voluntary", label: "Tự nguyện" },
  IMPROMPTU: { type: "impromptu", label: "Đột xuất" },
};

function normalizeUpper(value) {
  return String(value || "").trim().toUpperCase();
}

function isCompletedNote(note) {
  return String(note || "").trim().toLowerCase() === "hoàn thành phí";
}

export const PaymentHistoriesModal = ({
  isOpen,
  onClose,
  apartmentId,
  feeId,
}) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [items, setItems] = useState([]);
  const [page, setPage] = useState(1);
  const [limit, setLimit] = useState(10);
  const [totalItems, setTotalItems] = useState(0);

  const title = useMemo(() => {
    const base = "Lịch sử thanh toán";
    if (!apartmentId) return base;
    return `${base} (Căn hộ mã ${apartmentId})`;
  }, [apartmentId]);

  useEffect(() => {
    if (!isOpen) return;
    setPage(1);
  }, [isOpen]);

  useEffect(() => {
    if (!isOpen) return;
    if (!apartmentId) return;

    (async () => {
      setLoading(true);
      setError(null);
      try {
        const params = {
          page,
          limit,
          apartment_id: Number(apartmentId),
        };
        if (feeId) params.fee_id = Number(feeId);

        const resp = await getPayHistories(params);
        const payload = resp?.data ? resp.data : resp;
        setItems(payload?.pay_histories || []);
        setTotalItems(payload?.total_items || 0);
      } catch (e) {
        setError(e);
        setItems([]);
        setTotalItems(0);
      } finally {
        setLoading(false);
      }
    })();
  }, [isOpen, apartmentId, feeId, page, limit]);

  const renderCard = (p) => {
    const feeTypeKey = normalizeUpper(p?.fee_type_name);
    const feeTypeTag = feeTypeTagMap[feeTypeKey];
    const completed = isCompletedNote(p?.pay_note);

    return (
      <div className="pay-history-card" key={p?.pay_history_id}>
        <div className="pay-history-card__top">
          <div className="pay-history-card__title" title={p?.fee_name || ""}>
            {p?.fee_name || "(Không có tên khoản phí)"}
          </div>
          <div className="pay-history-card__top-right">
            {completed ? (
              <div className="pay-history-card__status">
                <Tag variant="Status" status="active">Hoàn thành phí</Tag>
              </div>
            ) : null}
            <div className="pay-history-card__amount">{formatCurrencyVND(p?.pay_amount)}</div>
          </div>
        </div>

        <div className="pay-history-card__meta">
          <div className="pay-history-card__meta-row">
            <span className="pay-history-card__label">Mã</span>
            <span className="pay-history-card__value">#{p?.pay_history_id ?? "-"}</span>
          </div>
          <div className="pay-history-card__meta-row">
            <span className="pay-history-card__label">Ngày thanh toán</span>
            <span className="pay-history-card__value">{p?.pay_datetime || "-"}</span>
          </div>
          <div className="pay-history-card__meta-row">
            <span className="pay-history-card__label">Loại phí</span>
            <span className="pay-history-card__value">
              {feeTypeTag ? (
                <Tag variant="Fee" type={feeTypeTag.type}>{feeTypeTag.label}</Tag>
              ) : (
                p?.fee_type_name || "-"
              )}
            </span>
          </div>
          <div className="pay-history-card__meta-row">
            <span className="pay-history-card__label">Danh mục</span>
            <span className="pay-history-card__value">{p?.fee_category_name || "-"}</span>
          </div>
          {p?.pay_note && !completed ? (
            <div className="pay-history-card__note" title={p?.pay_note}>
              {p?.pay_note}
            </div>
          ) : null}
        </div>
      </div>
    );
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={title}>
      {loading ? (
        <LoadingSpinner />
      ) : error ? (
        <div className="pay-history-state pay-history-state--error">
          Lấy lịch sử thanh toán thất bại
          {"\n"}
          {error?.message || String(error)}
        </div>
      ) : items.length === 0 ? (
        <div className="pay-history-state">Chưa có lịch sử thanh toán.</div>
      ) : (
        <>
          <div className="pay-history-list">{items.map(renderCard)}</div>
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
