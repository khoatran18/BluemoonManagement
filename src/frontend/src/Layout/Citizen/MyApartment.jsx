import React, { useCallback, useEffect, useMemo, useState } from "react";
import LoadingSpinner from "../../Components/LoadingSpinner/LoadingSpinner";
import Button from "../../Components/Button/Button";
import { getApartmentDetail } from "../../api/apartmentApi";
import { getMe } from "../../api/authApi";
import { usePersistentState } from "../../hooks/usePersistentState";
import "../Apartment/ApartmentManagement.css";

function toApartmentId(value) {
  const num = Number(String(value || "").trim());
  return Number.isFinite(num) && num > 0 ? num : null;
}

export default function MyApartment() {
  const [storedId, setStoredId] = usePersistentState("citizen.apartmentId", "");
  const [inputId, setInputId] = useState(String(storedId || ""));

  const apartmentId = useMemo(() => toApartmentId(storedId), [storedId]);

  const [meLoading, setMeLoading] = useState(true);
  const [meError, setMeError] = useState(null);
  const [me, setMe] = useState(null);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [data, setData] = useState(null);

  useEffect(() => {
    let mounted = true;
    (async () => {
      setMeLoading(true);
      setMeError(null);
      try {
        const me = await getMe();
        const meApartmentId = me?.apartment_id;
        if (!mounted) return;

        setMe(me);
        if (meApartmentId !== undefined && meApartmentId !== null && String(meApartmentId) !== "") {
          setStoredId(String(meApartmentId));
          setInputId(String(meApartmentId));
        }
      } catch {
        // Fallback to manual input.
        if (mounted) setMeError(true);
      } finally {
        if (mounted) setMeLoading(false);
      }
    })();
    return () => {
      mounted = false;
    };
  }, []);

  const fetchDetail = useCallback(async () => {
    if (!apartmentId) {
      setData(null);
      return;
    }

    setLoading(true);
    setError(null);
    try {
      const resp = await getApartmentDetail(apartmentId);
      setData(resp);
    } catch (e) {
      setError(e);
      setData(null);
    } finally {
      setLoading(false);
    }
  }, [apartmentId]);

  useEffect(() => {
    fetchDetail();
  }, [fetchDetail]);

  const onSave = () => {
    setStoredId(String(inputId || "").trim());
  };

  const meApartmentId = useMemo(() => toApartmentId(me?.apartment_id), [me]);
  const hasAutoApartment = !!meApartmentId;

  return (
    <div className="apartment-management-container">
      {!hasAutoApartment ? (
        <div className="filter-bar" style={{ marginBottom: 16 }}>
          <div className="search-box" style={{ flex: 1 }}>
            <input
              type="text"
              placeholder="Nhập Apartment ID (vd: 12)"
              value={inputId}
              onChange={(e) => setInputId(e.target.value)}
            />
          </div>
          <div className="filter-add-actions" style={{ display: "flex", gap: 8 }}>
            <Button onClick={onSave}>Lưu</Button>
          </div>
        </div>
      ) : null}

      {me && (me?.resident_id || me?.apartment_id) ? (
        <div style={{ padding: 16, paddingTop: 0, opacity: 0.85 }}>
          {me?.resident_id ? <span>Resident ID: <strong>{me.resident_id}</strong></span> : null}
          {me?.resident_id && me?.apartment_id ? <span> · </span> : null}
          {me?.apartment_id ? <span>Apartment ID: <strong>{me.apartment_id}</strong></span> : null}
        </div>
      ) : null}

      {!apartmentId ? (
        <div style={{ padding: 16 }}>
          {meLoading ? (
            "Đang lấy thông tin người dùng…"
          ) : (
            "Không lấy được Apartment ID từ hệ thống. Vui lòng nhập Apartment ID để xem thông tin căn hộ."
          )}
        </div>
      ) : null}

      {loading ? (
        <div style={{ padding: 16 }}>
          <LoadingSpinner />
        </div>
      ) : null}

      {error ? (
        <div style={{ padding: 16 }}>
          Không tải được dữ liệu: {String(error?.message || error)}
        </div>
      ) : null}

      {!loading && !error && data ? (
        <div style={{ padding: 16, display: "grid", gap: 12 }}>
          <div style={{ fontSize: 18, fontWeight: 600 }}>
            Căn hộ: {data?.building || ""}-{data?.room_number || ""} (ID: {apartmentId})
          </div>

          <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(240px, 1fr))", gap: 12 }}>
            <div className="overview-card">
              <div className="overview-card__title">Thông tin</div>
              <div className="overview-card__body">
                <div>Toà: <strong>{data?.building || ""}</strong></div>
                <div>Số phòng: <strong>{data?.room_number || ""}</strong></div>
                <div>Chủ hộ (Resident ID): <strong>{data?.head_resident_id ?? ""}</strong></div>
              </div>
            </div>

            <div className="overview-card">
              <div className="overview-card__title">Cư dân</div>
              <div className="overview-card__body">
                {(data?.residents || []).length === 0 ? (
                  <div>Chưa có dữ liệu cư dân.</div>
                ) : (
                  <ul style={{ margin: 0, paddingLeft: 18 }}>
                    {(data?.residents || []).map((r) => (
                      <li key={r?.resident_id || r?.id || JSON.stringify(r)}>
                        {r?.full_name || r?.name || ""}{r?.phone_number ? ` · ${r.phone_number}` : ""}
                      </li>
                    ))}
                  </ul>
                )}
              </div>
            </div>
          </div>
        </div>
      ) : null}
    </div>
  );
}
