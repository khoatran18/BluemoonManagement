import axiosClient from './axiosClient';

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
  try {
    const response = await axiosClient.get(`/fees`, {
      params
    });
    if (!response.data.success) {
      throw new Error(response.data.message);
    }
    return response.data.data;
  } catch (error) {
    console.error('Error fetching fees:', error);
    throw error;
  }
};

export const createFee = async (params) => {
    try{
        const response = await axiosClient.post("/fees", {
            fee_type_id: params.fee_type_id,
            fee_category_id: params.fee_category_id,
            fee_name: params.fee_name,
            fee_description: params.fee_description || "",
            fee_amount: params.fee_amount,
            applicable_month: params.applicable_month,
            effective_date: params.effective_date || null,
            expiry_date: params.expiry_date || null,
            status: params.status,
        });
        console.log("Create success")
         return response.data;
    } catch (error) {
        console.error('Error creating fees:', error);
        console.error(params)
        throw error;
    }
};

export const getFeeDetails = async (feeId) => {
    try {
        const response = await  axiosClient.get(`/fees/${feeId}`)

        return response.data
    } catch (error) {
        console.error('Error fetching fees:', error);
        throw error
    }
}

export const updateFee = async (feeId, params) => {
  try {
    const response = await axiosClient.put(`/fees/${feeId}`, {
      fee_id: feeId,
      fee_type_id: params.fee_type_id,
      fee_category_id: params.fee_category_id,
      fee_name: params.fee_name,
      fee_description: params.fee_description || "",
      fee_amount: params.fee_amount,
      applicable_month: params.applicable_month,
      effective_date: params.effective_date || null,
      expiry_date: params.expiry_date || null,
      status: params.status,
    });

    return response.data;
  } catch (error) {
    console.error('Error updating fee:', error);
    throw error;
  }
};

export const deleteFee = async (feeId) => {
  try {
    const response = await axiosClient.delete(`/fees/${feeId}`, { data: { fee_id: feeId } });
    return response.data;
  } catch (error) {
    console.error('Error deleting fee:', error);
    throw error;
  }
};

export const getFeeTypes = async () => {
  try {
    const response = await axiosClient.get(`/fee-types`);
    const raw = response && response.data ? response.data : null;
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
  } catch (error) {
    console.error('Error fetching fee types:', error);
    throw error;
  }
};

export const getFeeCategories = async (params = {}) => {
  try {
    const response = await axiosClient.get(`/fee-categories`, { params });
    const raw = response && response.data ? response.data : null;
    if (!raw) return [];

    if (Array.isArray(raw)) return raw;
    if (raw.success !== undefined && raw.data !== undefined) {
      const payload = raw.data;
      if (Array.isArray(payload)) return payload;
      const nested = findNestedArray(payload, 'fee-categories');
      console.log(nested)
      if (nested) return nested;
    }

    const direct = findNestedArray(raw, 'fee-categories');
    if (direct) return direct;

    return [];
  } catch (error) {
    console.error('Error fetching fee categories:', error);
    throw error;
  }
};