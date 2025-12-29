import axiosClient from './axiosClient';
import { apiCall } from './apiCall';

function findNestedArray(obj, key) {
  if (!obj || typeof obj !== 'object') return null;
  if (Array.isArray(obj)) return null;
  if (Object.prototype.hasOwnProperty.call(obj, key) && Array.isArray(obj[key])) return obj[key];

  for (const k of Object.keys(obj)) {
    const val = obj[k];
    if (val && typeof val === 'object') {
      const found = findNestedArray(val, key);
      if (found) return found;
    }
  }
  return null;
}

export const getFees = async (params = {}) => {
  return apiCall(
    () =>
      axiosClient.get(`/fees`, {
        params,
        paramsSerializer: {
          indexes: null,
        },
      }),
    { label: 'getFees' }
  );
};

export const createFee = async (params) => {
  return apiCall(
    () =>
      axiosClient.post('/fees', {
        fee_type_id: params.fee_type_id,
        fee_category_id: params.fee_category_id,
        fee_name: params.fee_name,
        fee_description: params.fee_description || '',
        fee_amount: params.fee_amount,
        applicable_month: params.applicable_month,
        effective_date: params.effective_date || null,
        expiry_date: params.expiry_date || null,
        status: params.status,
      }),
    {
      label: 'createFee',
      pick: (res) => res.data,
    }
  );
};

export const getFeeDetails = async (feeId) => {
  return apiCall(
    () => axiosClient.get(`/fees/${feeId}`),
    {
      label: 'getFeeDetails',
      pick: (res) => res.data,
    }
  );
};

export const updateFee = async (feeId, params) => {
  return apiCall(
    () =>
      axiosClient.put(`/fees/${feeId}`, {
        fee_id: feeId,
        fee_type_id: params.fee_type_id,
        fee_category_id: params.fee_category_id,
        fee_name: params.fee_name,
        fee_description: params.fee_description || '',
        fee_amount: params.fee_amount,
        applicable_month: params.applicable_month,
        effective_date: params.effective_date || null,
        expiry_date: params.expiry_date || null,
        status: params.status,
      }),
    {
      label: 'updateFee',
      pick: (res) => res.data,
    }
  );
};

export const deleteFee = async (feeId) => {
  return apiCall(
    () => axiosClient.delete(`/fees/${feeId}`, { data: { fee_id: feeId } }),
    {
      label: 'deleteFee',
      pick: (res) => res.data,
    }
  );
};

export const getFeeTypes = async () => {
  const raw = await apiCall(() => axiosClient.get(`/fee-types`), {
    label: 'getFeeTypes',
    requireSuccessFlag: false,
    pick: (res) => (res && res.data ? res.data : null),
  });

  if (!raw) return [];
  if (Array.isArray(raw)) return raw;

  if (raw.success !== undefined && raw.data !== undefined) {
    const payload = raw.data;
    if (Array.isArray(payload)) return payload;
    const nested = findNestedArray(payload, 'fee_types');
    if (nested) return nested;
  }

  const direct = findNestedArray(raw, 'fee_types');
  if (direct) return direct;

  return [];
};

export const getFeeCategories = async (params = {}) => {
  const raw = await apiCall(
    () => axiosClient.get(`/fee-categories`, { params }),
    {
      label: 'getFeeCategories',
      requireSuccessFlag: false,
      pick: (res) => (res && res.data ? res.data : null),
    }
  );

  if (!raw) return [];
  if (Array.isArray(raw)) return raw;

  if (raw.success !== undefined && raw.data !== undefined) {
    const payload = raw.data;
    if (Array.isArray(payload)) return payload;
    const nested = findNestedArray(payload, 'fee-categories');
    if (nested) return nested;
  }

  const direct = findNestedArray(raw, 'fee-categories');
  if (direct) return direct;

  return [];
};