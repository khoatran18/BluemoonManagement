import React, { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { createPortal } from "react-dom";
import "./EditApartmentModal.css";

import { getResidents } from "../../api/residentApi";
import { getApartmentDetail } from "../../api/apartmentApi";
import { ScrollableList } from "../ScrollableList";

export const EditApartmentModal = ({ isOpen, onClose, apartment, onSubmit }) => {
  const [formData, setFormData] = useState({
    id: "",
    building: "",
    room_number: "",
  });

  const [residentsLoading, setResidentsLoading] = useState(false);
  const [residentOptions, setResidentOptions] = useState([]);
  const [detailLoading, setDetailLoading] = useState(false);

  const [residentQuery, setResidentQuery] = useState("");
  const [isResidentDropdownOpen, setIsResidentDropdownOpen] = useState(false);
  const residentInputRef = useRef(null);
  const residentDropdownRef = useRef(null);
  const [residentDropdownPos, setResidentDropdownPos] = useState({ top: 0, left: 0, width: 0 });

  const [selectedResidents, setSelectedResidents] = useState([]);
  const [headResidentId, setHeadResidentId] = useState(null);

  useEffect(() => {
    if (!isOpen || !apartment) return;
    setFormData({
      id: apartment.id || "",
      building: apartment.building || "",
      room_number: apartment.room_number || apartment.room || "",
    });
  }, [apartment, isOpen]);

  useEffect(() => {
    if (!isOpen) return;
    let cancelled = false;

    (async () => {
      setResidentsLoading(true);
      try {
        const resp = await getResidents({ page: 1, limit: 1000 });

        let payload = resp;
        if (payload && payload.data) payload = payload.data;
        if (payload && payload.data) payload = payload.data;

        const list = Array.isArray(payload?.residents)
          ? payload.residents
          : Array.isArray(payload?.items)
            ? payload.items
            : Array.isArray(payload)
              ? payload
              : [];

        if (!cancelled) setResidentOptions(list);
      } catch (_) {
        if (!cancelled) setResidentOptions([]);
      } finally {
        if (!cancelled) setResidentsLoading(false);
      }
    })();

    return () => {
      cancelled = true;
    };
  }, [isOpen]);

  useEffect(() => {
    if (!isOpen || !apartment?.id) return;
    let cancelled = false;

    (async () => {
      setDetailLoading(true);
      try {
        const resp = await getApartmentDetail(apartment.id);

        let payload = resp;
        if (payload && payload.data) payload = payload.data;
        if (payload && payload.data) payload = payload.data;

        const residents = Array.isArray(payload?.residents) ? payload.residents : [];
        const normalizedResidents = residents
          .map((r) => ({
            resident_id: r?.resident_id ?? r?.id,
            full_name: r?.full_name ?? r?.name ?? "",
          }))
          .filter((r) => r.resident_id !== undefined && r.resident_id !== null);

        if (!cancelled) {
          setFormData((prev) => ({
            ...prev,
            id: payload?.apartment_id ?? payload?.id ?? prev.id,
            building: payload?.building ?? prev.building,
            room_number: payload?.room_number ?? prev.room_number,
          }));

          setSelectedResidents(normalizedResidents);
          setHeadResidentId(payload?.head_resident_id ?? null);
        }
      } catch (_) {
        // keep whatever we have from table row
      } finally {
        if (!cancelled) setDetailLoading(false);
      }
    })();

    return () => {
      cancelled = true;
    };
  }, [isOpen, apartment?.id]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const residentOptionsById = useMemo(() => {
    const map = new Map();
    for (const r of residentOptions) {
      const id = r?.resident_id ?? r?.id;
      if (id !== undefined && id !== null) map.set(String(id), r);
    }
    return map;
  }, [residentOptions]);

  const filteredResidentOptions = useMemo(() => {
    const q = (residentQuery || "").trim().toLowerCase();
    if (!q) return residentOptions;
    return residentOptions.filter((r) => {
      const fullName = String(r?.full_name ?? "").toLowerCase();
      const phone = String(r?.phone_number ?? "").toLowerCase();
      const email = String(r?.email ?? "").toLowerCase();
      const id = String(r?.resident_id ?? r?.id ?? "").toLowerCase();
      return fullName.includes(q) || phone.includes(q) || email.includes(q) || id.includes(q);
    });
  }, [residentOptions, residentQuery]);

  // Ensure head resident exists in selected list when possible.
  useEffect(() => {
    if (!headResidentId) return;
    const headId = String(headResidentId);
    const exists = selectedResidents.some((r) => String(r?.resident_id ?? r?.id) === headId);
    if (exists) return;
    const fromOptions = residentOptionsById.get(headId);
    if (!fromOptions) return;
    setSelectedResidents((prev) => [
      ...prev,
      { resident_id: fromOptions?.resident_id ?? fromOptions?.id, full_name: fromOptions?.full_name ?? fromOptions?.name ?? "" },
    ]);
  }, [headResidentId, residentOptionsById, selectedResidents]);

  const updateResidentDropdownPosition = useCallback(() => {
    const inputEl = residentInputRef.current;
    if (!inputEl) return;
    const rect = inputEl.getBoundingClientRect();
    const dropdownMaxHeight = 220;
    const gap = 6;
    const shouldFlip = rect.bottom + gap + dropdownMaxHeight > window.innerHeight;
    const top = shouldFlip ? Math.max(6, rect.top - gap - dropdownMaxHeight) : rect.bottom + gap;
    setResidentDropdownPos({ top, left: rect.left, width: rect.width });
  }, []);

  useEffect(() => {
    if (!isResidentDropdownOpen) return;
    updateResidentDropdownPosition();
    const handleResize = () => updateResidentDropdownPosition();
    const handleScroll = () => updateResidentDropdownPosition();
    window.addEventListener("resize", handleResize);
    window.addEventListener("scroll", handleScroll, true);
    return () => {
      window.removeEventListener("resize", handleResize);
      window.removeEventListener("scroll", handleScroll, true);
    };
  }, [isResidentDropdownOpen, updateResidentDropdownPosition]);

  useEffect(() => {
    if (!isResidentDropdownOpen) return;
    const handleMouseDown = (e) => {
      const inputEl = residentInputRef.current;
      const dropdownEl = residentDropdownRef.current;
      const target = e.target;
      if (inputEl && inputEl.contains(target)) return;
      if (dropdownEl && dropdownEl.contains(target)) return;
      setIsResidentDropdownOpen(false);
    };
    document.addEventListener("mousedown", handleMouseDown);
    return () => document.removeEventListener("mousedown", handleMouseDown);
  }, [isResidentDropdownOpen]);

  const handleAddResidentToSelection = (resident) => {
    if (!resident) return;
    const id = resident?.resident_id ?? resident?.id;
    if (id === undefined || id === null) return;
    const exists = selectedResidents.some((r) => String(r?.resident_id ?? r?.id) === String(id));
    if (exists) return;
    const next = [
      ...selectedResidents,
      { resident_id: id, full_name: resident?.full_name ?? resident?.name ?? "" },
    ];
    setSelectedResidents(next);
    if (!headResidentId) setHeadResidentId(id);
    setResidentQuery("");
    setIsResidentDropdownOpen(false);
  };

  const handleRemoveSelectedResident = (residentId) => {
    const id = String(residentId);
    const next = selectedResidents.filter((r) => String(r?.resident_id ?? r?.id) !== id);
    setSelectedResidents(next);
    if (String(headResidentId ?? "") === id) {
      const fallback = next[0]?.resident_id ?? next[0]?.id ?? null;
      setHeadResidentId(fallback);
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (onSubmit) {
      if (selectedResidents.length > 0 && !headResidentId) return;

      onSubmit({
        ...formData,
        head_resident_id: headResidentId,
        residents: selectedResidents.map((r) => ({ id: r?.resident_id ?? r?.id })),
      });
    }
  };

  if (!isOpen || !apartment) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content edit-apartment-modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2 className="modal-title">Chỉnh sửa thông tin căn hộ</h2>
          <button
            className="modal-close"
            onClick={onClose}
            aria-label="Đóng modal"
          >
            ✕
          </button>
        </div>

        <form className="edit-form" onSubmit={handleSubmit}>
          <div className="form-body">
            <div className="form-group">
              <label htmlFor="id">Mã căn hộ</label>
              <input
                type="text"
                id="id"
                name="id"
                value={formData.id}
                onChange={handleChange}
                placeholder="Nhập mã căn hộ"
                required
                disabled
              />
            </div>

            <div className="form-row">
              <div className="form-group">
                <label htmlFor="building">Tòa nhà</label>
                <input
                  type="text"
                  id="building"
                  name="building"
                  value={formData.building}
                  onChange={handleChange}
                  placeholder="Nhập tòa nhà"
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="room_number">Số phòng</label>
                <input
                  type="text"
                  id="room_number"
                  name="room_number"
                  value={formData.room_number}
                  onChange={handleChange}
                  placeholder="Nhập số phòng"
                  required
                />
              </div>
            </div>

            <div className="form-group">
              <label htmlFor="edit_apartment_resident_select">Chọn cư dân</label>
              <div className="edit-apartment-resident-picker">
                <input
                  id="edit_apartment_resident_select"
                  ref={residentInputRef}
                  type="text"
                  value={residentQuery}
                  onChange={(e) => {
                    setResidentQuery(e.target.value);
                    setIsResidentDropdownOpen(true);
                  }}
                  onFocus={() => {
                    setIsResidentDropdownOpen(true);
                    updateResidentDropdownPosition();
                  }}
                  placeholder={residentsLoading ? "Đang tải danh sách cư dân..." : "Tìm theo tên / SĐT / email"}
                  disabled={residentsLoading || detailLoading}
                  autoComplete="off"
                />
              </div>

              {isResidentDropdownOpen && !residentsLoading
                ? createPortal(
                    <ScrollableList
                      ref={residentDropdownRef}
                      className="edit-apartment-resident-dropdown"
                      role="listbox"
                      style={{
                        position: "fixed",
                        top: residentDropdownPos.top,
                        left: residentDropdownPos.left,
                        width: residentDropdownPos.width,
                      }}
                    >
                      {filteredResidentOptions.length === 0 ? (
                        <div className="edit-apartment-resident-dropdown-empty">Không tìm thấy cư dân phù hợp.</div>
                      ) : (
                        filteredResidentOptions.slice(0, 50).map((r) => {
                          const id = r?.resident_id ?? r?.id;
                          const name = r?.full_name ?? r?.name ?? String(id ?? "");
                          const sub = r?.phone_number || r?.email || "";
                          if (id === undefined || id === null) return null;

                          const alreadySelected = selectedResidents.some(
                            (sr) => String(sr?.resident_id ?? sr?.id) === String(id)
                          );

                          return (
                            <button
                              key={String(id)}
                              type="button"
                              className="edit-apartment-resident-dropdown-item"
                              onMouseDown={(ev) => ev.preventDefault()}
                              onClick={() => handleAddResidentToSelection(residentOptionsById.get(String(id)))}
                              disabled={alreadySelected}
                              title={alreadySelected ? "Đã được chọn" : "Chọn cư dân"}
                            >
                              <div className="edit-apartment-resident-item-main">{name}</div>
                              {sub ? <div className="edit-apartment-resident-item-sub">{sub}</div> : null}
                            </button>
                          );
                        })
                      )}
                    </ScrollableList>,
                    document.body
                  )
                : null}
            </div>

            <div className="form-group">
              <label>Danh sách cư dân (chọn 1 chủ hộ)</label>

              {selectedResidents.length === 0 ? (
                <div className="edit-apartment-hint">Chưa chọn cư dân nào.</div>
              ) : (
                <div className="edit-apartment-selected-residents">
                  {selectedResidents.map((r) => {
                    const id = r?.resident_id ?? r?.id;
                    const name = r?.full_name ?? r?.name ?? String(id ?? "");
                    return (
                      <div key={String(id)} className="edit-apartment-selected-row">
                        <label className="edit-apartment-selected-radio">
                          <input
                            type="radio"
                            name="edit_apartment_head_resident"
                            checked={String(headResidentId ?? "") === String(id)}
                            onChange={() => setHeadResidentId(id)}
                          />
                          <span>{name}</span>
                        </label>
                        <button
                          type="button"
                          className="edit-apartment-selected-remove"
                          onClick={() => handleRemoveSelectedResident(id)}
                        >
                          Bỏ
                        </button>
                      </div>
                    );
                  })}
                </div>
              )}

              {selectedResidents.length > 0 && !headResidentId ? (
                <div className="edit-apartment-hint">Vui lòng chọn 1 chủ hộ.</div>
              ) : null}
            </div>
          </div>

          <div className="form-footer">
            <button
              type="button"
              className="btn-cancel"
              onClick={onClose}
            >
              Hủy
            </button>
            <button
              type="submit"
              className="btn-submit"
            >
              Cập nhật
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
