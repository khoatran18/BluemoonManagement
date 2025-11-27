import React from "react";
import "./Pagination.css";
import next from "../../assets/icon/table/next.svg"
import back from "../../assets/icon/table/back.svg"

export default function Pagination({
  total,
  pageSize,
  current,
  onChange
}) {
    const totalPages = Math.ceil(total / pageSize);

    const getPages = () => {
        let pages = [];

        if (totalPages <= 7) {
        for (let i = 1; i <= totalPages; i++) pages.push(i);
        return pages;
        }

        if (current <= 4) return [1, 2, 3, 4, "...", totalPages - 1, totalPages];

        if (current >= totalPages - 3)
        return [1, 2, "...", totalPages - 3, totalPages - 2, totalPages - 1, totalPages];

        return [
        1,
        "...",
        current - 1,
        current,
        current + 1,
        "...",
        totalPages
        ];
    };

    const pages = getPages();

    return (
        <div className="pagination-wrapper">

        {/* Previous */}
        <button
            className="paginate-btn"
            disabled={current === 1}
            onClick={() => onChange(current - 1)}
        >
            <div className="btn-content">
                <div className="icon-holder"><img className="arrow" src={back} alt="Back" /></div>
                <div>Trước</div>
            </div> 
        </button>

        {/* Page numbers */}
        <div className="paginate-numbers">
            {pages.map((p, idx) =>
            p === "..." ? (
                <span key={idx} className="dots">…</span>
            ) : (
                <button
                key={idx}
                className={`page-num ${p === current ? "active" : ""}`}
                onClick={() => onChange(p)}
                >
                {p}
                </button>
            )
            )}
        </div>

        {/* Next */}
        <button
            className="paginate-btn"
            disabled={current === totalPages}
            onClick={() => onChange(current + 1)}
        >
            <div className="btn-content">
                <div>Tiếp</div>
                <div className="icon-holder"><img className="arrow" src={next} alt="Next" /></div>
            </div>
        </button>

        </div>
    );
}
