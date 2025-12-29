import axiosClient from "./axiosClient";
import { apiCall } from "./apiCall";

const apartmentFeeStatusCache = new Map();

export const peekApartmentFeeStatusCache = (apartmentId) => {
  if (apartmentId === undefined || apartmentId === null || apartmentId === "") return null;
  return apartmentFeeStatusCache.get(String(apartmentId)) || null;
};

export const clearApartmentFeeStatusCache = (apartmentId) => {
  if (apartmentId === undefined || apartmentId === null || apartmentId === "") {
    apartmentFeeStatusCache.clear();
    return;
  }
  apartmentFeeStatusCache.delete(String(apartmentId));
};

export const getApartmentFeeStatus = async (apartmentId, options = {}) => {
  if (apartmentId === undefined || apartmentId === null || apartmentId === "") {
    throw new Error("apartmentId is required");
  }

  const { useCache = true, force = false } = options;
  const key = String(apartmentId);
  if (useCache && !force && apartmentFeeStatusCache.has(key)) {
    return apartmentFeeStatusCache.get(key);
  }

  const data = await apiCall(() => axiosClient.get(`/apartment-fee-statuses/${apartmentId}`), {
    label: "getApartmentFeeStatus",
  });

  if (useCache) apartmentFeeStatusCache.set(key, data);
  return data;
};
