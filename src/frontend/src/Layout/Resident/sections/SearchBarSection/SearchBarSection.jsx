import React, { useState } from "react";
import { Modal } from "../../../../Components/Modal";
import { AddResidentForm } from "../../../../Components/AddResidentForm";
import { SuccessModal } from "../../../../Components/SuccessModal";
import { createResident } from "../../../../api/residentApi";

export const SearchBarSection = ({ onSearchChange, onRefresh }) => {
  const [searchQuery, setSearchQuery] = useState("");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isSuccessModalOpen, setIsSuccessModalOpen] = useState(false);
  const [successData, setSuccessData] = useState(null);
  const [errorMessage, setErrorMessage] = useState("");

  const handleSearchChange = (e) => {
    const query = e.target.value;
    setSearchQuery(query);
    console.log("Search input changed:", query);
    if (onSearchChange) {
      onSearchChange(query);
    }
  };

  const handleFilterClick = () => {
    console.log("Filter button clicked");
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
      console.log("Submitting form data:", formData);
      const response = await createResident(formData);
      console.log("Response from server:", response);
      
      setSuccessData(formData);
      setIsSuccessModalOpen(true);
      setIsModalOpen(false);
      setErrorMessage("");
      console.log("Thêm cư dân thành công:", formData);
      
      // Notify parent component to refresh data
      if (onSearchChange) {
        onSearchChange("");
      }
      if (onRefresh) {
        onRefresh();
      }
    } catch (err) {
      console.error('Error creating resident:', err);
      const errorMsg = err.response?.data?.message || err.message || "Có lỗi xảy ra khi thêm cư dân";
      setErrorMessage(errorMsg);
    }
  };

  return (
    <header className="relative self-stretch w-full h-14 bg-[#ffffff]">
      {/* Search Input */}
      <div className="flex w-80 h-10 items-center gap-2 px-3 py-1.5 absolute top-3 left-[90px] bg-[#ffffff] rounded-md overflow-hidden shadow-[0px_0px_0px_1px_#68708229,0px_1px_2px_#0000000f]">
        <img
          className="relative w-4 h-4"
          alt=""
          src="https://c.animaapp.com/iSVrHgil/img/search.svg"
          aria-hidden="true"
        />

        <label htmlFor="apartment-search" className="sr-only">
          Tìm kiếm cư dân
        </label>
        <input
          id="apartment-search"
          type="search"
          value={searchQuery}
          onChange={handleSearchChange}
          placeholder="Tìm kiếm cư dân"
          className="relative flex-1 font-body-b2 font-[number:var(--body-b2-font-weight)] text-greygrey-900 text-[length:var(--body-b2-font-size)] tracking-[var(--body-b2-letter-spacing)] leading-[var(--body-b2-line-height)] [font-style:var(--body-b2-font-style)] placeholder:text-gray-300 outline-none"
          aria-label="Tìm kiếm cư dân"
        />
      </div>

      {/* Buttons Container - Side by Side */}
      <div className="flex gap-2 absolute left-0 top-3 h-10">
        {/* Filter Button */}
        <button
          className="box-border px-4 py-[9.5px] inline-flex items-center justify-center gap-2 rounded-lg border border-solid border-greygrey-400 cursor-pointer hover:bg-greygrey-50 transition-colors w-20"
          onClick={handleFilterClick}
          type="button"
          aria-label="Lọc kết quả tìm kiếm"
        >
          <img
            className="relative w-5 h-5"
            alt=""
            src="https://c.animaapp.com/iSVrHgil/img/filter.svg"
            aria-hidden="true"
          />

          <span className="relative font-body-2-medium font-[number:var(--body-2-medium-font-weight)] text-greygrey-500 text-[length:var(--body-2-medium-font-size)] tracking-[var(--body-2-medium-letter-spacing)] leading-[var(--body-2-medium-line-height)] whitespace-nowrap [font-style:var(--body-2-medium-font-style)]">
            Lọc
          </span>
        </button>

      </div>
      <div className="flex gap-2 absolute right-0 top-3 h-10">
        {/* Add Button */}
        <button
          className="group box-border px-4 py-[9.5px] inline-flex items-center justify-center gap-2 rounded-lg border border-solid border-blue-600 bg-blue-600 cursor-pointer hover:bg-blue-700 transition-colors w-48"
          onClick={handleAddResidentClick}
          type="button"
          aria-label="Thêm cư dân"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            strokeWidth={1}
            stroke="currentColor"
            fill="currentColor"
            viewBox="0 0 24 24"
            className="relative w-4 h-4 text-white transition-transform duration-300 group-hover:rotate-90 group-hover:scale-110"
            aria-hidden="true"
          >
            <path fillRule="evenodd" d="M12 2a1 1 0 011 1v8h8a1 1 0 110 2h-8v8a1 1 0 11-2 0v-8H3a1 1 0 110-2h8V3a1 1 0 011-1z" clipRule="evenodd" />
          </svg>

          <span className="relative font-body-2-medium font-[number:var(--body-2-medium-font-weight)] text-white text-[length:var(--body-2-medium-font-size)] tracking-[var(--body-2-medium-letter-spacing)] leading-[var(--body-2-medium-line-height)] whitespace-nowrap [font-style:var(--body-2-medium-font-style)]">
            THÊM CƯ DÂN
          </span>
        </button>
      </div>

      <Modal
        isOpen={isModalOpen}
        onClose={handleModalClose}
        title="Thêm dân cư mới"
      >
        {errorMessage && (
          <div className="error-banner" style={{
            backgroundColor: "#fee2e2",
            border: "1px solid #fecaca",
            borderRadius: "6px",
            padding: "12px 16px",
            marginBottom: "16px",
            color: "#dc2626",
            fontSize: "14px"
          }}>
            {errorMessage}
          </div>
        )}
        <AddResidentForm
          onSubmit={handleAddResidentSubmit}
          onCancel={handleModalClose}
        />
      </Modal>

      <SuccessModal
        isOpen={isSuccessModalOpen}
        onClose={() => setIsSuccessModalOpen(false)}
        message="Thêm dân cư thành công!"
        residentData={successData}
      />
    </header>
  );
};
