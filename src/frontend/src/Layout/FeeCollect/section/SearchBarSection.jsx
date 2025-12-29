import React, { useState } from "react";

// FeeCollect uses the same visual search bar style as Apartment,
// but without the "THÊM CĂN HỘ" button / modal.
export const FeeCollectSearchBarSection = ({ onSearchChange }) => {
  const [searchQuery, setSearchQuery] = useState("");

  const handleSearchChange = (e) => {
    const query = e.target.value;
    setSearchQuery(query);
    if (onSearchChange) onSearchChange(query);
  };

  const handleFilterClick = () => {
    console.log("Filter button clicked");
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

        <label htmlFor="fee-collect-search" className="sr-only">
          Tìm kiếm
        </label>
        <input
          id="fee-collect-search"
          type="search"
          value={searchQuery}
          onChange={handleSearchChange}
          placeholder="Tìm kiếm"
          className="relative flex-1 font-body-b2 font-[number:var(--body-b2-font-weight)] text-greygrey-900 text-[length:var(--body-b2-font-size)] tracking-[var(--body-b2-letter-spacing)] leading-[var(--body-b2-line-height)] [font-style:var(--body-b2-font-style)] placeholder:text-gray-300 outline-none"
          aria-label="Tìm kiếm"
        />
      </div>

      {/* Filter Button */}
      <div className="flex gap-2 absolute left-0 top-3 h-10">
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
    </header>
  );
};
