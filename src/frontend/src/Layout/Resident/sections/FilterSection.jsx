import { useEffect, useRef, useState } from "react";
import filterIcon from "../../../assets/icon/fee/filter.svg";
import searchIcon from "../../../assets/icon/fee/search.svg";
import "../../Fee/sections/FilterSection.css";

import AddButton from "../../../Components/Button/AddButton";
import { Modal } from "../../../Components/Modal";
import { AddResidentForm } from "../../../Components/AddResidentForm";
import { SuccessModal } from "../../../Components/SuccessModal";
import { createResident } from "../../../api/residentApi";

export default function FilterSection({
  search = "",
  apartmentId = "",
  phoneNumber = "",
  email = "",
  onSearchChange,
  onApartmentIdChange,
  onPhoneNumberChange,
  onEmailChange,
  onRefresh,
  onNotify,
}) {
  const [open, setOpen] = useState(false);
  const containerRef = useRef(null);

  const [localSearch, setLocalSearch] = useState(search || "");

  const [localApartmentId, setLocalApartmentId] = useState(apartmentId || "");
  const [localPhoneNumber, setLocalPhoneNumber] = useState(phoneNumber || "");
  const [localEmail, setLocalEmail] = useState(email || "");

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isSuccessModalOpen, setIsSuccessModalOpen] = useState(false);
  const [successData, setSuccessData] = useState(null);
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() => setLocalSearch(search || ""), [search]);
  useEffect(() => setLocalApartmentId(apartmentId || ""), [apartmentId]);
  useEffect(() => setLocalPhoneNumber(phoneNumber || ""), [phoneNumber]);
  useEffect(() => setLocalEmail(email || ""), [email]);

  useEffect(() => {
    const id = setTimeout(() => {
      if (typeof onSearchChange === "function") onSearchChange(localSearch);
    }, 300);
    return () => clearTimeout(id);
  }, [localSearch]);

  useEffect(() => {
    if (!open) return;
    const handleClickOutside = (e) => {
      if (containerRef.current && !containerRef.current.contains(e.target)) setOpen(false);
    };
    document.addEventListener("click", handleClickOutside);
    document.addEventListener("touchstart", handleClickOutside);
    return () => {
      document.removeEventListener("click", handleClickOutside);
      document.removeEventListener("touchstart", handleClickOutside);
    };
  }, [open]);

  const handleReset = () => {
    setLocalSearch("");
    setLocalApartmentId("");
    setLocalPhoneNumber("");
    setLocalEmail("");

    if (typeof onSearchChange === "function") onSearchChange("");
    if (typeof onApartmentIdChange === "function") onApartmentIdChange("");
    if (typeof onPhoneNumberChange === "function") onPhoneNumberChange("");
    if (typeof onEmailChange === "function") onEmailChange("");

    setOpen(false);
  };

  const handleApply = () => {
    if (typeof onApartmentIdChange === "function") onApartmentIdChange(localApartmentId);
    if (typeof onPhoneNumberChange === "function") onPhoneNumberChange(localPhoneNumber);
    if (typeof onEmailChange === "function") onEmailChange(localEmail);
    setOpen(false);
  };

  const handleAddResidentClick = () => {
    setIsModalOpen(true);
    setErrorMessage("");
  };

  const handleModalClose = () => {
    setIsModalOpen(false);
    setErrorMessage("");
  };

  const handleAddResidentSubmit = async (formData) => {
    try {
      const response = await createResident(formData);
      console.log("Response from server:", response);

      setSuccessData(formData);
      setIsSuccessModalOpen(true);
      setIsModalOpen(false);
      setErrorMessage("");

      if (typeof onNotify === "function") {
        onNotify({ message: "Thêm cư dân thành công", variant: "success", duration: 3000 });
      }

      if (typeof onSearchChange === "function") onSearchChange("");
      if (typeof onRefresh === "function") onRefresh();
    } catch (err) {
      console.error("Error creating resident:", err);
      const errorMsg = err?.response?.data?.message || err?.message || "Có lỗi xảy ra khi thêm cư dân";
      setErrorMessage(errorMsg);

      if (typeof onNotify === "function") {
        onNotify({ message: errorMsg, variant: "error", duration: 4000 });
      }
    }
  };

  return (
    <div className="filter-bar" ref={containerRef}>
      <button className="filter-btn" onClick={() => setOpen((prev) => !prev)} type="button">
        <img src={filterIcon} alt="Filter" />
        <span>Lọc</span>
      </button>

      <div className="search-box">
        <img src={searchIcon} alt="Search" className="search-icon" />
        <input
          type="text"
          placeholder="Tìm kiếm cư dân"
          value={localSearch}
          onChange={(e) => setLocalSearch(e.target.value)}
        />
      </div>

      {open && (
        <div className="filter-panel">
          <div className="panel-title">Bộ lọc</div>

          <div className="panel-section">
            <div className="panel-title">Căn hộ</div>
            <div className="time-filters">
              <label>
                Apartment ID
                <input
                  type="number"
                  value={localApartmentId}
                  onChange={(e) => setLocalApartmentId(e.target.value)}
                  placeholder="Nhập apartment_id"
                />
              </label>
            </div>
          </div>

          <div className="panel-section">
            <div className="panel-title">Liên hệ</div>
            <div className="time-filters">
              <label>
                Số điện thoại
                <input
                  type="text"
                  value={localPhoneNumber}
                  onChange={(e) => setLocalPhoneNumber(e.target.value)}
                  placeholder="Nhập số điện thoại"
                />
              </label>
              <label>
                Email
                <input
                  type="text"
                  value={localEmail}
                  onChange={(e) => setLocalEmail(e.target.value)}
                  placeholder="Nhập email"
                />
              </label>
            </div>
          </div>

          <div className="filter-panel-actions">
            <button type="button" className="btn-reset" onClick={handleReset}>
              Đặt lại
            </button>
            <button type="button" className="btn-confirm" onClick={handleApply}>
              Áp dụng
            </button>
            <button type="button" className="btn-close" onClick={() => setOpen(false)}>
              Đóng
            </button>
          </div>
        </div>
      )}

      <div className="filter-add-actions">
        <AddButton className="filter-add-btn" onClick={handleAddResidentClick}>
          THÊM CƯ DÂN
        </AddButton>
      </div>

      <Modal isOpen={isModalOpen} onClose={handleModalClose} title="Thêm dân cư mới">
        {errorMessage && (
          <div
            className="error-banner"
            style={{
              backgroundColor: "#fee2e2",
              border: "1px solid #fecaca",
              borderRadius: "6px",
              padding: "12px 16px",
              marginBottom: "16px",
              color: "#dc2626",
              fontSize: "14px",
            }}
          >
            {errorMessage}
          </div>
        )}
        <AddResidentForm onSubmit={handleAddResidentSubmit} onCancel={handleModalClose} />
      </Modal>

      <SuccessModal
        isOpen={isSuccessModalOpen}
        onClose={() => setIsSuccessModalOpen(false)}
        message="Thêm dân cư thành công!"
        residentData={successData}
      />
    </div>
  );
}
