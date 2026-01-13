import axiosClient from "./axiosClient";
import { apiCall } from "./apiCall";

const normalizeVehicleList = (list) => {
  if (!Array.isArray(list)) return [];

  const numbers = list
    .map((v) => {
      if (typeof v === "string") return v;
      if (v && typeof v === "object") return v.number;
      return "";
    })
    .map((s) => String(s || "").trim())
    .filter(Boolean);

  const unique = [];
  const seen = new Set();
  for (const n of numbers) {
    const key = n.toLowerCase();
    if (seen.has(key)) continue;
    seen.add(key);
    unique.push({ number: n });
  }
  return unique;
};

export const getApartments = async (params = {}) => {
        return apiCall(
            () =>
                axiosClient.get(`/apartments`, {
                    params,
                    paramsSerializer: {
                        indexes: null,
                    },
                }),
            { label: 'getApartments' }
        );
}

export const getApartmentDetail = async (apartmentId) => {
        return apiCall(
            () => axiosClient.get(`/apartments/${apartmentId}`),
            { label: 'getApartmentDetail' }
        );
}

export const createApartment = async (apartmentData) => {
  return apiCall(
    () =>
      axiosClient.post('/apartments', {
        building: apartmentData.building,
        room_number: apartmentData.room_number,
        head_resident_id: apartmentData.head_resident_id ?? null,
        residents: Array.isArray(apartmentData.residents)
          ? apartmentData.residents.map((r) => ({ id: r?.id }))
          : [],
        motors: normalizeVehicleList(apartmentData?.motors),
        cars: normalizeVehicleList(apartmentData?.cars),
      }),
    {
      label: 'createApartment',
      pick: (res) => res.data,
    }
  );
};

export const editApartment = async (apartmentId, apartmentData) => {
  return apiCall(
    () =>
      axiosClient.put(`/apartments/${apartmentId}`, {
        building: apartmentData.building,
        room_number: apartmentData.room_number,
        head_resident_id: apartmentData.head_resident_id ?? null,
        residents: Array.isArray(apartmentData.residents)
          ? apartmentData.residents.map((r) => ({ id: r?.id }))
          : [],
        motors: normalizeVehicleList(apartmentData?.motors),
        cars: normalizeVehicleList(apartmentData?.cars),
      }),
    {
      label: 'editApartment',
      pick: (res) => res.data,
    }
  );
};

export const deleteApartment = async (apartmentId) => {
  return apiCall(
    () => axiosClient.delete(`/apartments/${apartmentId}`, { data: { id: apartmentId } }),
    {
      label: 'deleteApartment',
      pick: (res) => res.data,
    }
  );
};

export const getDeleteApartmentHistories = async (params = {}) => {
  return apiCall(
    () =>
      axiosClient.get(`/delete-apartment-histories`, {
        params,
        paramsSerializer: {
          indexes: null,
        },
      }),
    { label: 'getDeleteApartmentHistories' }
  );
};

export const getDeleteApartmentHistoryDetail = async (historyId) => {
  return apiCall(() => axiosClient.get(`/delete-apartment-histories/${historyId}`), {
    label: 'getDeleteApartmentHistoryDetail',
  });
};

export const getApartmentSpecificAdjustmentsByApartmentId = async (apartmentId) => {
  if (apartmentId === undefined || apartmentId === null || apartmentId === "") {
    throw new Error("apartmentId is required");
  }

  return apiCall(
    () => axiosClient.get(`/apartments/apartment_specific_adjustments/${apartmentId}`),
    { label: "getApartmentSpecificAdjustmentsByApartmentId" }
  );
};

export const updateApartmentSpecificAdjustmentsByApartmentId = async (apartmentId, adjustmentIds) => {
  if (apartmentId === undefined || apartmentId === null || apartmentId === "") {
    throw new Error("apartmentId is required");
  }

  return apiCall(
    () =>
      axiosClient.put(`/apartments/apartment_specific_adjustments/${apartmentId}`, {
        adjustment_ids: Array.isArray(adjustmentIds) ? adjustmentIds : [],
      }),
    { label: "updateApartmentSpecificAdjustmentsByApartmentId" }
  );
};

