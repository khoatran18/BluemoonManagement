import { useEffect, useRef, useState } from "react";
import filterIcon from "../../../assets/icon/fee/filter.svg";
import searchIcon from "../../../assets/icon/fee/search.svg";
import "../../Fee/sections/FilterSection.css";

export default function FilterSection({ onSearchChange }) {
  const [open, setOpen] = useState(false);
  const containerRef = useRef(null);
  const [localSearch, setLocalSearch] = useState("");

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
    if (typeof onSearchChange === "function") onSearchChange("");
    setOpen(false);
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
          placeholder="Tìm kiếm"
          value={localSearch}
          onChange={(e) => setLocalSearch(e.target.value)}
        />
      </div>

      {open && (
        <div className="filter-panel">
          <div className="panel-title">Bộ lọc</div>
          <div className="panel-section">
            <div>Chưa hỗ trợ bộ lọc cho thu phí.</div>
          </div>
          <div className="filter-panel-actions">
            <button type="button" className="btn-reset" onClick={handleReset}>
              Đặt lại
            </button>
            <button type="button" className="btn-close" onClick={() => setOpen(false)}>
              Đóng
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
