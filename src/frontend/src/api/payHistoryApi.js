import axiosClient from "./axiosClient";
import { apiCall } from "./apiCall";

export const getPayHistories = async (params = {}) => {
  return apiCall(
    () =>
      axiosClient.get(`/pay-histories`, {
        params,
        paramsSerializer: {
          indexes: null,
        },
      }),
    {
      label: "getPayHistories",
      pick: (res) => (res?.data?.data ? res.data.data : res.data),
    }
  );
};

// NOTE: API contract specifies this endpoint under adjustments
export const getPayHistoryDetails = async (payHistoryId) => {
  return apiCall(
    () => axiosClient.get(`/adjustments/pay-histories/${payHistoryId}`),
    {
      label: "getPayHistoryDetails",
      pick: (res) => (res?.data?.data ? res.data.data : res.data),
    }
  );
};
