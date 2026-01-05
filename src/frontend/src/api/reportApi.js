import axiosClient from "./axiosClient";
import { apiCall } from "./apiCall";

export const getApartmentCommonReport = async () => {
  return apiCall(() => axiosClient.get("/reports/apartment_common"), {
    label: "getApartmentCommonReport",
  });
};

export const getFeeCommonReport = async () => {
  return apiCall(() => axiosClient.get("/reports/fee_common"), {
    label: "getFeeCommonReport",
  });
};
