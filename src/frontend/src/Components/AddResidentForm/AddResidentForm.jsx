import React, { useEffect, useState } from "react";
import "./AddResidentForm.css";
import { getApartments } from "../../api/apartmentApi";

export const AddResidentForm = ({ onSubmit, onCancel }) => {
  const [formData, setFormData] = useState({
    full_name: "",
    email: "",
    phone_number: "",
    apartment_id: "",
  });

  const [apartments, setApartments] = useState([]);
  const [apartmentsLoading, setApartmentsLoading] = useState(false);
  const [apartmentsError, setApartmentsError] = useState("");

  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);

  const parseApartmentId = (value) => {
    const raw = String(value || "").trim();
    if (!raw) return null;

    if (/^\d+$/.test(raw)) return Number(raw);

    const match = raw.match(/#\s*(\d+)\s*\)?$/);
    if (match && match[1]) return Number(match[1]);

    return null;
  };

  useEffect(() => {
    let mounted = true;
    const fetchApartments = async () => {
      setApartmentsLoading(true);
      setApartmentsError("");
      try {
        const res = await getApartments({ page: 1, limit: 1000 });
        const items = res?.items || [];
        if (mounted) setApartments(items);
      } catch (err) {
        if (mounted) {
          setApartments([]);
          setApartmentsError("Không thể tải danh sách căn hộ");
        }
      } finally {
        if (mounted) setApartmentsLoading(false);
      }
    };

    fetchApartments();
    return () => {
      mounted = false;
    };
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
    // Clear error for this field
    if (errors[name]) {
      setErrors((prev) => ({
        ...prev,
        [name]: "",
      }));
    }
  };

  const validateForm = () => {
    const newErrors = {};
    const apartmentId = parseApartmentId(formData.apartment_id);
    if (!String(formData.apartment_id || "").trim()) {
      newErrors.apartment_id = "Mã căn hộ không được bỏ trống";
    } else if (!apartmentId) {
      newErrors.apartment_id = "Vui lòng chọn căn hộ hợp lệ";
    }
    if (!formData.full_name.trim()) {
      newErrors.full_name = "Tên cư dân không được bỏ trống";
    }
    if (formData.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      newErrors.email = "Email không hợp lệ";
    }
    return newErrors;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const newErrors = validateForm();
    
    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    setIsSubmitting(true);
    try {
      if (onSubmit) {
        const apartmentId = parseApartmentId(formData.apartment_id);
        const payload = {
          ...formData,
          apartment_id: apartmentId,
        };
        await onSubmit(payload);
      }
      setFormData({
        full_name: "",
        email: "",
        phone_number: "",
        apartment_id: "",
      });
      setErrors({});
    } catch (error) {
      console.error("Error submitting form:", error);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <form className="add-resident-form" onSubmit={handleSubmit}>
      <div className="form-group">
        <label htmlFor="full_name">Tên cư dân</label>
        <input
          type="text"
          id="full_name"
          name="full_name"
          value={formData.full_name}
          onChange={handleChange}
          placeholder="Nhập tên cư dân"
          disabled={isSubmitting}
        />
        {errors.full_name && <span className="error-text">{errors.full_name}</span>}
      </div>

      <div className="form-group">
        <label htmlFor="email">Email</label>
        <input
          type="email"
          id="email"
          name="email"
          value={formData.email}
          onChange={handleChange}
          placeholder="Nhập email"
          disabled={isSubmitting}
        />
        {errors.email && <span className="error-text">{errors.email}</span>}
      </div>

      <div className="form-group">
        <label htmlFor="phone_number">Số điện thoại</label>
        <input
          type="tel"
          id="phone_number"
          name="phone_number"
          value={formData.phone_number}
          onChange={handleChange}
          placeholder="Nhập số điện thoại"
          disabled={isSubmitting}
        />
      </div>

      <div className="form-group">
        <label htmlFor="apartment_id">Căn hộ</label>
        <input
          type="text"
          id="apartment_id"
          name="apartment_id"
          list="apartment-options"
          value={formData.apartment_id}
          onChange={handleChange}
          placeholder={
            apartmentsLoading
              ? "Đang tải danh sách căn hộ..."
              : "Gõ để tìm (VD: A - 101) hoặc nhập mã (#12)"
          }
          disabled={isSubmitting}
        />
        <datalist id="apartment-options">
          {apartments.map((apartment) => (
            <option
              key={apartment.apartment_id}
              value={`${apartment.building} - ${apartment.room_number} (#${apartment.apartment_id})`}
            />
          ))}
        </datalist>
        {errors.apartment_id && <span className="error-text">{errors.apartment_id}</span>}
        {!errors.apartment_id && apartmentsError && (
          <span className="error-text">{apartmentsError}</span>
        )}
      </div>

      <div className="form-actions">
        <button
          type="button"
          className="btn-cancel"
          onClick={onCancel}
          disabled={isSubmitting}
        >
          Hủy
        </button>
        <button
          type="submit"
          className="btn-submit"
          disabled={isSubmitting}
        >
          {isSubmitting ? "Đang thêm..." : "Thêm cư dân"}
        </button>
      </div>
    </form>
  );
};
