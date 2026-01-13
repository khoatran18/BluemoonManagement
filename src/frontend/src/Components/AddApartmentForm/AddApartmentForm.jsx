import React, { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { createPortal } from "react-dom";
import "./AddApartmentForm.css";

import { getResidents } from "../../api/residentApi";
import { ScrollableList } from "../ScrollableList";

export const AddApartmentForm = ({ onSubmit, onCancel }) => {
  const [formData, setFormData] = useState({
    building: "",
    room_number: "",
  });

  const [motorNumbersText, setMotorNumbersText] = useState("");
  const [carNumbersText, setCarNumbersText] = useState("");

  const [residentsLoading, setResidentsLoading] = useState(false);
  const [residentOptions, setResidentOptions] = useState([]);
  const [residentQuery, setResidentQuery] = useState("");
  const [isResidentDropdownOpen, setIsResidentDropdownOpen] = useState(false);
  const residentInputRef = useRef(null);
  const residentDropdownRef = useRef(null);
  const [residentDropdownPos, setResidentDropdownPos] = useState({ top: 0, left: 0, width: 0 });
  const [selectedResidents, setSelectedResidents] = useState([]);
  const [headResidentId, setHeadResidentId] = useState(null);

  useEffect(() => {
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
      } catch (err) {
        if (!cancelled) setResidentOptions([]);
      } finally {
        if (!cancelled) setResidentsLoading(false);
      }
    })();

    return () => {
      cancelled = true;
    };
  }, []);

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

  const updateResidentDropdownPosition = useCallback(() => {
    const inputEl = residentInputRef.current;
    if (!inputEl) return;

    const rect = inputEl.getBoundingClientRect();
    const dropdownMaxHeight = 220;
    const gap = 6;

    const shouldFlip = rect.bottom + gap + dropdownMaxHeight > window.innerHeight;
    const top = shouldFlip ? Math.max(6, rect.top - gap - dropdownMaxHeight) : rect.bottom + gap;

    setResidentDropdownPos({
      top,
      left: rect.left,
      width: rect.width,
    });
  }, []);

  useEffect(() => {
    if (!isResidentDropdownOpen) return;
    updateResidentDropdownPosition();

    const handleResize = () => updateResidentDropdownPosition();
    // capture scroll events from modal/body
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

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (onSubmit) {
      if (selectedResidents.length > 0 && !headResidentId) {
        return;
      }

      const parseNumbers = (text) => {
        return String(text || "")
          .split(/[\n,;]+/g)
          .map((s) => s.trim())
          .filter(Boolean);
      };

      onSubmit({
        ...formData,
        head_resident_id: headResidentId,
        residents: selectedResidents.map((r) => ({ id: r?.resident_id ?? r?.id })),
        motors: parseNumbers(motorNumbersText).map((number) => ({ number })),
        cars: parseNumbers(carNumbersText).map((number) => ({ number })),
      });
    }
    setFormData({
      building: "",
      room_number: "",
    });

    setResidentQuery("");
    setIsResidentDropdownOpen(false);
    setSelectedResidents([]);
    setHeadResidentId(null);
    setMotorNumbersText("");
    setCarNumbersText("");
  };

  const handleAddResidentToSelection = (resident) => {
    if (!resident) return;
    const id = String(resident?.resident_id ?? resident?.id);
    if (!id) return;

    const exists = selectedResidents.some((r) => String(r?.resident_id ?? r?.id) === id);
    if (exists) return;

    const next = [...selectedResidents, resident];
    setSelectedResidents(next);
    if (!headResidentId) setHeadResidentId(resident?.resident_id ?? resident?.id);
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

  return (
    <form className="add-apartment-form" onSubmit={handleSubmit}>
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
          <label htmlFor="room">Số phòng</label>
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

      <div className="form-row">
        <div className="form-group" style={{ width: "100%" }}>
          <label htmlFor="resident_select">Chọn cư dân</label>
          <div className="resident-picker">
            <input
              id="resident_select"
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
              disabled={residentsLoading}
              autoComplete="off"
            />
          </div>
        </div>
      </div>

      {isResidentDropdownOpen && !residentsLoading
        ? createPortal(
            <ScrollableList
              ref={residentDropdownRef}
              className="resident-dropdown"
              role="listbox"
              style={{
                position: "fixed",
                top: residentDropdownPos.top,
                left: residentDropdownPos.left,
                width: residentDropdownPos.width,
              }}
            >
              {filteredResidentOptions.length === 0 ? (
                <div className="resident-dropdown-empty">Không tìm thấy cư dân phù hợp.</div>
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
                      className="resident-dropdown-item"
                      onMouseDown={(e) => e.preventDefault()}
                      onClick={() => handleAddResidentToSelection(residentOptionsById.get(String(id)))}
                      disabled={alreadySelected}
                      title={alreadySelected ? "Đã được chọn" : "Chọn cư dân"}
                    >
                      <div className="resident-item-main">{name}</div>
                      {sub ? <div className="resident-item-sub">{sub}</div> : null}
                    </button>
                  );
                })
              )}
            </ScrollableList>,
            document.body
          )
        : null}

      <div className="form-row">
        <div className="form-group" style={{ width: "100%" }}>
          <label>Danh sách cư dân (chọn 1 chủ hộ)</label>

          {selectedResidents.length === 0 ? (
            <div style={{ opacity: 0.8 }}>Chưa chọn cư dân nào.</div>
          ) : (
            <div className="selected-residents">
              {selectedResidents.map((r) => {
                const id = r?.resident_id ?? r?.id;
                const name = r?.full_name ?? r?.name ?? String(id ?? "");
                return (
                  <div key={String(id)} className="selected-resident-row">
                    <label className="selected-resident-radio">
                      <input
                        type="radio"
                        name="head_resident"
                        checked={String(headResidentId ?? "") === String(id)}
                        onChange={() => setHeadResidentId(id)}
                      />
                      <span>{name}</span>
                    </label>
                    <button
                      type="button"
                      className="selected-resident-remove"
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
            <div className="form-hint">Vui lòng chọn 1 chủ hộ.</div>
          ) : null}
        </div>
      </div>

      <div className="form-row">
        <div className="form-group" style={{ width: "100%" }}>
          <label htmlFor="motor_numbers">Xe máy (mỗi dòng hoặc cách nhau bằng dấu phẩy)</label>
          <textarea
            id="motor_numbers"
            value={motorNumbersText}
            onChange={(e) => setMotorNumbersText(e.target.value)}
            placeholder="VD: A12-334\nB12-332"
            rows={3}
          />
        </div>
      </div>

      <div className="form-row">
        <div className="form-group" style={{ width: "100%" }}>
          <label htmlFor="car_numbers">Ô tô (mỗi dòng hoặc cách nhau bằng dấu phẩy)</label>
          <textarea
            id="car_numbers"
            value={carNumbersText}
            onChange={(e) => setCarNumbersText(e.target.value)}
            placeholder="VD: C12-334\nD12-332"
            rows={3}
          />
        </div>
      </div>

      <div className="form-actions">
        <button
          type="button"
          className="btn-cancel"
          onClick={onCancel}
        >
          Hủy
        </button>
        <button
          type="submit"
          className="btn-submit"
        >
          Thêm căn hộ
        </button>
      </div>
    </form>
  );
};
