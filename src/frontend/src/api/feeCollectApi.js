import axiosClient from "./axiosClient";
import { apiCall } from "./apiCall";

export const getApartmentFeeStatus = async (apartmentId) => {
  if (apartmentId === undefined || apartmentId === null || apartmentId === "") {
    throw new Error("apartmentId is required");
  }

  const data = await apiCall(() => axiosClient.get(`/apartment-fee-statuses/${apartmentId}`), {
    label: "getApartmentFeeStatus",
  });
  return data;
};

export const updateApartmentFeeStatus = async (apartmentId, payload) => {
  if (apartmentId === undefined || apartmentId === null || apartmentId === "") {
    throw new Error("apartmentId is required");
  }

  const data = await apiCall(() => axiosClient.put(`/apartment-fee-statuses/${apartmentId}`, payload), {
    label: "updateApartmentFeeStatus",
  });
  return data;
};
