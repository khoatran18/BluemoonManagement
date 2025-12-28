import React, { useState, useEffect } from "react";
import "./AddFeeForm.css";

export const AddFeeForm = ({ initial = {}, feeTypes = [], feeCategories = [], onSubmit, onCancel, isEditing = false }) => {
  const [formData, setFormData] = useState({
    fee_type_id: initial.fee_type_id || (feeTypes[0] && feeTypes[0].id) || "",
    fee_category_id: initial.fee_category_id || (feeCategories[0] && feeCategories[0].fee_category_id) || "",
    fee_name: initial.fee_name || "",
    fee_description: initial.fee_description || "",
    fee_amount: initial.fee_amount || "",
    applicable_month: initial.applicable_month || "",
    effective_date: initial.effective_date || "",
    expiry_date: initial.expiry_date || "",
    status: initial.status || "active",
  });

  const [monthRange, setMonthRange] = useState({ start: "", end: "" });
  const [loading, setLoading] = useState(false);

  const pad = (v) => String(v).padStart(2, "0");
  const formatDate = (d) => {
    const yyyy = d.getFullYear();
    const mm = pad(d.getMonth() + 1);
    const dd = pad(d.getDate());
    return `${yyyy}-${mm}-${dd}`;
  };

  const MAX_AMOUNT = 10000000;
  const formatVND = (n) => {
    if (n === "" || n == null || isNaN(Number(n))) return "";
    return new Intl.NumberFormat('vi-VN').format(Number(n));
  };

  useEffect(() => {
    setFormData({
      fee_type_id: initial.fee_type_id || (feeTypes[0] && feeTypes[0].id) || "",
      fee_category_id: initial.fee_category_id || (feeCategories[0] && feeCategories[0].fee_category_id) || "",
      fee_name: initial.fee_name || "",
      fee_description: initial.fee_description || "",
      fee_amount: initial.fee_amount || "",
      applicable_month: initial.applicable_month || "",
      effective_date: initial.effective_date || "",
      expiry_date: initial.expiry_date || "",
      status: initial.status || "ACTIVE",
    });
  }, [initial, feeTypes, feeCategories]);

  useEffect(() => {
    const m = formData.applicable_month; // expected format YYYY-MM
    if (!m) {
      setMonthRange({ start: "", end: "" });
      return;
    }
    const [y, mm] = m.split("-");
    const year = Number(y);
    const month = Number(mm);
    const start = `${y}-${pad(mm)}-01`;
    const lastDay = new Date(year, month, 0).getDate();
    const end = `${y}-${pad(mm)}-${pad(lastDay)}`;
    setMonthRange({ start, end });
    setFormData(prev => ({ ...prev, effective_date: "", expiry_date: "" }));
  }, [formData.applicable_month]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    if (name === "fee_amount") {
      // allow only digits, cap to MAX_AMOUNT
      const digits = (value || "").replace(/\D/g, "");
      const num = digits === "" ? "" : String(Math.min(Number(digits), MAX_AMOUNT));
      // keep numeric raw value in state but we display formatted text in the input
      setFormData(prev => ({ ...prev, fee_amount: num }));
      return;
    }
      if (name === "applicable_month") {
        setFormData((prev) => ({ ...prev, [name]: value }));
        return;
      }

      if (name === "effective_date") {
        // enforce effective date inside chosen month
        if (monthRange.start) {
          if (value < monthRange.start || value > monthRange.end) {
            // ignore invalid selection
            setFormData(prev => ({ ...prev, effective_date: "", expiry_date: "" }));
            return;
          }
        }
        // if expiry exists and is not after new effective date, clear expiry
        setFormData(prev => ({ ...prev, effective_date: value, expiry_date: (prev.expiry_date && prev.expiry_date > value) ? prev.expiry_date : "" }));
        return;
      }

      if (name === "expiry_date") {
        // ensure expiry is after effective date
        if (formData.effective_date && !(value > formData.effective_date)) {
          // ignore invalid expiry
          return;
        }
        setFormData(prev => ({ ...prev, expiry_date: value }));
        return;
      }

      setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const submit = (e) => {
    e.preventDefault();
      // Validation: applicable month required for date selection
      if (!formData.applicable_month) {
        alert("Vui lòng chọn Tháng áp dụng trước khi chọn ngày hiệu lực/ hạn.");
        return;
      }

      if (!formData.effective_date) {
        alert("Vui lòng chọn Ngày hiệu lực trong tháng đã chọn.");
        return;
      }

      if (formData.expiry_date && !(formData.expiry_date > formData.effective_date)) {
        alert("Ngày hết hạn phải sau Ngày hiệu lực.");
        return;
      }

      // convert fee_amount to number before submitting
      const payload = { ...formData, fee_amount: formData.fee_amount === "" ? 0 : Number(formData.fee_amount) };

      if (!onSubmit) return;

      (async () => {
        try {
          setLoading(true);
          await Promise.resolve(onSubmit(payload));
        } catch (err) {
          // swallow here; caller may handle errors
          console.error('AddFeeForm submit error', err);
        } finally {
          setLoading(false);
        }
      })();
  };

  return (
    <form className="add-fee-form" onSubmit={submit}>
      <div className="form-group">
        <label htmlFor="fee_type_id">Loại phí</label>
        <select id="fee_type_id" name="fee_type_id" value={formData.fee_type_id} onChange={handleChange} disabled={isEditing || loading} required>
          {feeTypes.map(ft => (
            <option key={ft.id} value={ft.id}>{ft.name}</option>
          ))}
        </select>
      </div>

      <div className="form-group">
        <label htmlFor="fee_category_id">Danh mục</label>
        <select id="fee_category_id" name="fee_category_id" value={formData.fee_category_id} onChange={handleChange} disabled={isEditing || loading} required>
          {feeCategories.map(fc => (
            <option key={fc.fee_category_id} value={fc.fee_category_id}>{fc.name}</option>
          ))}
        </select>
      </div>

      <div className="form-group">
        <label htmlFor="fee_name">Tên khoản phí</label>
        <input id="fee_name" name="fee_name" value={formData.fee_name} onChange={handleChange} required disabled={isEditing || loading} />
      </div>

      <div className="form-group fee-amount-group">
        <label htmlFor="fee_amount">Số tiền</label>
        <div className="fee-amount-input-wrap">
          <input
            id="fee_amount"
            name="fee_amount"
            type="text"
            inputMode="numeric"
            value={formData.fee_amount ? formatVND(formData.fee_amount) : ''}
            onChange={handleChange}
            onBlur={() => {
              // ensure value is within cap (already enforced) and keep numeric raw value in state
              if (formData.fee_amount === "") return;
              setFormData(prev => ({ ...prev, fee_amount: String(Math.min(Number(prev.fee_amount || 0), MAX_AMOUNT)) }));
            }}
            required
            disabled={loading}
          />
          <span className="fee-amount-unit">₫</span>
        </div>
      </div>

      <div className="form-group">
        <label htmlFor="applicable_month">Tháng áp dụng</label>
        <input
          id="applicable_month"
          name="applicable_month"
          type="month"
          value={formData.applicable_month}
          onChange={handleChange}
          required
          onKeyDown={(e) => e.preventDefault()}
          disabled={loading}
        />
      </div>

      <div className="form-group">
        <label htmlFor="effective_date">Ngày hiệu lực</label>
          <input
            id="effective_date"
            name="effective_date"
            type="date"
            value={formData.effective_date}
            onChange={handleChange}
            disabled={!monthRange.start || loading}
            min={monthRange.start || undefined}
            max={monthRange.end || undefined}
          />
      </div>

      <div className="form-group">
        <label htmlFor="expiry_date">Ngày hết hạn</label>
          <input
            id="expiry_date"
            name="expiry_date"
            type="date"
            value={formData.expiry_date}
            onChange={handleChange}
            disabled={!monthRange.start || !formData.effective_date || loading}
            min={formData.effective_date ? formatDate(new Date(new Date(formData.effective_date).getTime() + 24*60*60*1000)) : (monthRange.start || undefined)}
          />
      </div>

      <div className="form-group">
        <label htmlFor="fee_description">Mô tả</label>
        <textarea id="fee_description" name="fee_description" value={formData.fee_description} onChange={handleChange} disabled={loading} />
      </div>

      <div className="form-group">
        <label htmlFor="status">Trạng thái</label>
        <select id="status" name="status" value={formData.status} onChange={handleChange} required disabled={loading}>
          <option value="ACTIVE">Đang hoạt động</option>
          <option value="DRAFT">Nháp</option>
          <option value="CLOSED">Đã đóng</option>
          <option value="ARCHIVED">Lưu trữ</option>
        </select>
      </div>

      <div className="form-actions">
        <button type="button" className="btn-cancel" onClick={onCancel} disabled={loading}>Hủy</button>
        <button type="submit" className="btn-submit" disabled={loading}>
          {loading ? (<><span className="spinner" style={{marginRight:8}}></span>Đang xử lý...</>) : (isEditing ? 'Cập nhật' : 'Tạo mới')}
        </button>
      </div>
    </form>
  );
};

export default AddFeeForm;
