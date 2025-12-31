import React, { useCallback, useState } from "react";
import "./DetailApartmentModal.css";

import { getResidentDetail } from "../../api/residentApi";

export const DetailApartmentModal = ({ isOpen, onClose, apartment, loading = false }) => {
  const [residentDetailsById, setResidentDetailsById] = useState({});
  const [residentDetailLoadingById, setResidentDetailLoadingById] = useState({});
  const [residentDetailErrorById, setResidentDetailErrorById] = useState({});

  const fetchResidentDetailIfNeeded = useCallback(async (residentId) => {
    if (residentId === null || residentId === undefined || residentId === "") return;
    if (residentDetailsById[residentId]) return;
    if (residentDetailLoadingById[residentId]) return;

    setResidentDetailLoadingById((prev) => ({ ...prev, [residentId]: true }));
    setResidentDetailErrorById((prev) => ({ ...prev, [residentId]: null }));

    try {
      const resp = await getResidentDetail(residentId);

      let payload = resp;
      if (payload && payload.data) payload = payload.data;
      if (payload && payload.data) payload = payload.data;

      const apartmentPayload = payload?.apartment || null;
      const normalized = {
        id: payload?.resident_id ?? payload?.id ?? residentId,
        full_name: payload?.full_name ?? payload?.name ?? "",
        email: payload?.email ?? "",
        phone_number: payload?.phone_number ?? payload?.phone ?? "",
        is_head: !!(payload?.is_head ?? payload?.isHead),
        apartment: apartmentPayload,
        building: apartmentPayload?.building ?? payload?.building ?? "",
        room_number: apartmentPayload?.room_number ?? payload?.room_number ?? payload?.room ?? "",
      };

      setResidentDetailsById((prev) => ({ ...prev, [residentId]: normalized }));
    } catch (err) {
      setResidentDetailErrorById((prev) => ({ ...prev, [residentId]: err?.message || "Tải chi tiết cư dân thất bại" }));
    } finally {
      setResidentDetailLoadingById((prev) => ({ ...prev, [residentId]: false }));
    }
  }, [residentDetailsById, residentDetailLoadingById]);

  if (!isOpen) return null;

  const residentCount =
    typeof apartment?.resident_count === "number"
      ? apartment.resident_count
      : Array.isArray(apartment?.residents)
        ? apartment.residents.length
        : null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content detail-modal" onClick={(e) => e.stopPropagation()}>
        <div className="detail-header">
          <h2 className="detail-title">Chi tiết căn hộ</h2>
          <button className="modal-close" onClick={onClose} aria-label="Đóng modal">
            ✕
          </button>
        </div>

        <div className="detail-body">
          <div className="detail-card">
            {loading ? (
              <div className="detail-section">
                <h3 className="section-title">Đang tải</h3>
                <p className="detail-value">Đang tải dữ liệu căn hộ...</p>
              </div>
            ) : !apartment ? (
              <div className="detail-section">
                <h3 className="section-title">Không có dữ liệu</h3>
                <p className="detail-value">Không lấy được thông tin căn hộ.</p>
              </div>
            ) : (
              <>
                <div className="detail-section">
                  <h3 className="section-title">Thông tin cơ bản</h3>

                  <div className="detail-grid">
                    <div className="detail-item">
                      <label>Mã căn hộ</label>
                      <p className="detail-value">{apartment.id ?? "—"}</p>
                    </div>

                    <div className="detail-item">
                      <label>Tòa nhà</label>
                      <p className="detail-value">{apartment.building ?? "—"}</p>
                    </div>

                    <div className="detail-item">
                      <label>Số phòng</label>
                      <p className="detail-value">{apartment.room_number ?? apartment.room ?? "—"}</p>
                    </div>

                    <div className="detail-item">
                      <label>Số cư dân</label>
                      <p className="detail-value">{typeof residentCount === "number" ? residentCount : "—"}</p>
                    </div>
                  </div>
                </div>

                <div className="detail-section">
                  <h3 className="section-title">Chủ hộ</h3>

                  <div className="detail-grid">
                    <div className="detail-item">
                      <label>Tên</label>
                      <p className="detail-value">{apartment.head_resident?.full_name || "—"}</p>
                    </div>

                    <div className="detail-item">
                      <label>Mã cư dân</label>
                      <p className="detail-value">
                        {apartment.head_resident?.resident_id ?? apartment.head_resident_id ?? "—"}
                      </p>
                    </div>
                  </div>
                </div>

                {Array.isArray(apartment.residents) && (
                  <div className="detail-section">
                    <h3 className="section-title">Danh sách cư dân</h3>
                    {apartment.residents.length === 0 ? (
                      <p className="detail-value">Chưa có cư dân.</p>
                    ) : (
                      <div className="resident-list">
                        {apartment.residents.slice(0, 8).map((r) => (
                          <details
                            className="resident-dropdown"
                            key={r.resident_id ?? r.id ?? r.full_name}
                            onToggle={(e) => {
                              const id = r.resident_id ?? r.id ?? null;
                              if (e.currentTarget.open) fetchResidentDetailIfNeeded(id);
                            }}
                          >
                            <summary className="resident-dropdown-summary">
                              <div className="resident-summary-left">
                                <span className="resident-summary-name">{r.full_name || r.name || "—"}</span>
                              </div>

                              <div className="resident-summary-right">
                                <span className="resident-summary-id">{r.resident_id ?? r.id ?? "—"}</span>
                              </div>
                            </summary>

                            <div className="resident-dropdown-body">
                              {(() => {
                                const id = r.resident_id ?? r.id ?? null;
                                if (id === null) {
                                  return <p className="detail-value">Không có mã cư dân để tải chi tiết.</p>;
                                }

                                if (residentDetailLoadingById[id]) {
                                  return <p className="detail-value">Đang tải chi tiết...</p>;
                                }

                                const error = residentDetailErrorById[id];
                                if (error) {
                                  return <p className="detail-value">{error}</p>;
                                }

                                const detail = residentDetailsById[id];
                                if (!detail) {
                                  return <p className="detail-value">Mở dropdown để tải chi tiết.</p>;
                                }

                                return (
                                  <div className="resident-detail-stack">
                                    <div className="detail-item">
                                      <label>Mã cư dân</label>
                                      <p className="detail-value">{detail.id ?? "—"}</p>
                                    </div>

                                    <div className="detail-item">
                                      <label>Email</label>
                                      <p className="detail-value">{detail.email || "—"}</p>
                                    </div>

                                    <div className="detail-item">
                                      <label>SĐT</label>
                                      <p className="detail-value">{detail.phone_number || "—"}</p>
                                    </div>

                                    <div className="detail-item">
                                      <label>Vai trò</label>
                                      <p className="detail-value">{detail.is_head ? "Chủ hộ" : "Cư dân"}</p>
                                    </div>

                                    <div className="detail-item">
                                      <label>Căn hộ</label>
                                      <p className="detail-value">
                                        {detail.building || detail.room_number
                                          ? `${detail.building || ""}${detail.building && detail.room_number ? " - " : ""}${detail.room_number || ""}`
                                          : "—"}
                                      </p>
                                    </div>
                                  </div>
                                );
                              })()}
                            </div>
                          </details>
                        ))}
                      </div>
                    )}
                  </div>
                )}
              </>
            )}
          </div>
        </div>

        <div className="detail-footer">
          <button className="btn-close-detail" onClick={onClose}>
            Đóng
          </button>
        </div>
      </div>
    </div>
  );
};
