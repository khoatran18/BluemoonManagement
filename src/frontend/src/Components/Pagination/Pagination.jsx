import React from "react";
import "./Pagination.css";
import next from "../../assets/icon/table/next.svg";
import back from "../../assets/icon/table/back.svg";

export default function Pagination({
  total,
  page,
  limit,
  onPageChange,
  onLimitChange
}) {
  const totalPages = Math.ceil(total / limit);

  const getPages = () => {
    let pages = [];

    if (totalPages <= 7) {
      for (let i = 1; i <= totalPages; i++) pages.push(i);
      return pages;
    }

    if (page <= 4) return [1, 2, 3, 4, "...", totalPages - 1, totalPages];

    if (page >= totalPages - 3)
      return [
        1,
        2,
        "...",
        totalPages - 3,
        totalPages - 2,
        totalPages - 1,
        totalPages
      ];

    return [1, "...", page - 1, page, page + 1, "...", totalPages];
  };

  const pages = getPages();

  const handleLimitChange = (e) => {
    const newLimit = Number(e.target.value);
    onLimitChange?.(newLimit);
    onPageChange?.(1); // reset về trang 1 khi đổi limit
  };

  return (
    <div className="pagination-wrapper">

      {/* PREV BUTTON */}
      <button
        className="paginate-btn"
        disabled={page === 1}
        onClick={() => onPageChange(page - 1)}
      >
        <div className="btn-content">
          <div className="icon-holder">
            <img className="arrow" src={back} alt="Back" />
          </div>
          <div>Trước</div>
        </div>
      </button>

      {/* PAGE NUMBERS */}
      <div className="paginate-numbers">
        {pages.map((p, idx) =>
          p === "..." ? (
            <span key={idx} className="dots">…</span>
          ) : (
            <button
              key={idx}
              className={`page-num ${p === page ? "active" : ""}`}
              onClick={() => onPageChange(p)}
            >
              {p}
            </button>
          )
        )}
      </div>

      <div className="limit-selector">
        <span>Hiển thị</span>
        <select value={limit} onChange={handleLimitChange}>
          <option value="3">3</option>
          <option value="5">5</option>
          <option value="10">10</option>
          <option value="20">20</option>
        </select>
        <span>/ trang</span>
      </div>

      {/* NEXT BUTTON */}
      <button
        className="paginate-btn"
        disabled={page === totalPages}
        onClick={() => onPageChange(page + 1)}
      >
        <div className="btn-content">
          <div>Tiếp</div>
          <div className="icon-holder">
            <img className="arrow" src={next} alt="Next" />
          </div>
        </div>
      </button>
    </div>
  );
}
