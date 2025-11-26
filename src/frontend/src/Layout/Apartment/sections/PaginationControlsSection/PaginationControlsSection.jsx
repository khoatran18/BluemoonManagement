import React, { useState } from "react";

export const PaginationControlsSection = () => {
  const [currentPage, setCurrentPage] = useState(1);

  const pageNumbers = [
    { value: 1, display: "1" },
    { value: 2, display: "2" },
    { value: 3, display: "3" },
    { value: 4, display: "4" },
    { value: null, display: "..." },
    { value: 6, display: "6" },
    { value: 7, display: "7" },
  ];

  const handlePageClick = (pageValue) => {
    if (pageValue !== null) {
      setCurrentPage(pageValue);
    }
  };

  const handlePrevious = () => {
    if (currentPage > 1) {
      setCurrentPage(currentPage - 1);
    }
  };

  const handleNext = () => {
    setCurrentPage(currentPage + 1);
  };

  const isFirstPage = currentPage === 1;

  return (
    <nav
      className="flex items-start justify-around relative self-stretch w-full flex-[0_0_auto]"
      aria-label="Pagination"
    >
      <div className="flex w-[1020px] items-center justify-between pt-[11px] pb-4 px-6 relative ml-[-24.50px] mr-[-24.50px]">
        <button
          className="all-[unset] box-border pl-2 pr-4 py-[7.5px] inline-flex items-center justify-center gap-2 relative flex-[0_0_auto] rounded-lg border border-solid border-greygrey-400 cursor-pointer disabled:cursor-not-allowed disabled:opacity-20"
          onClick={handlePrevious}
          disabled={isFirstPage}
          aria-label="Previous page"
        >
          <img
            className="relative w-5 h-5"
            alt=""
            src="https://c.animaapp.com/iSVrHgil/img/chevron-left.svg"
            aria-hidden="true"
          />
          <span className="relative w-fit mt-[-1.00px] font-body-2-medium font-[number:var(--body-2-medium-font-weight)] text-greygrey-500 text-[length:var(--body-2-medium-font-size)] tracking-[var(--body-2-medium-letter-spacing)] leading-[var(--body-2-medium-line-height)] whitespace-nowrap [font-style:var(--body-2-medium-font-style)]">
            Trước
          </span>
        </button>

        <div
          className="inline-flex items-start gap-0.5 relative flex-[0_0_auto]"
          role="list"
        >
          {pageNumbers.map((page, index) => (
            <button
              key={index}
              className={`flex flex-col w-10 h-10 items-center justify-center gap-2 px-3.5 py-3 relative rounded-lg ${
                page.value === currentPage ? "bg-greygrey-50" : ""
              } ${page.value !== null ? "cursor-pointer hover:bg-greygrey-50" : "cursor-default"}`}
              onClick={() => handlePageClick(page.value)}
              disabled={page.value === null}
              aria-label={
                page.value !== null ? `Page ${page.value}` : "More pages"
              }
              aria-current={page.value === currentPage ? "page" : undefined}
              role="listitem"
            >
              <span
                className={`relative w-fit mt-[-3.00px] mb-[-1.00px] font-text-sm-medium font-[number:var(--text-sm-medium-font-weight)] text-[length:var(--text-sm-medium-font-size)] text-center tracking-[var(--text-sm-medium-letter-spacing)] leading-[var(--text-sm-medium-line-height)] whitespace-nowrap [font-style:var(--text-sm-medium-font-style)] ${
                  page.value === currentPage
                    ? "text-primaryprimary-600"
                    : "text-greygrey-400"
                }`}
              >
                {page.display}
              </span>
            </button>
          ))}
        </div>

        <button
          className="all-[unset] box-border pl-4 pr-2 py-[7.5px] inline-flex items-center justify-center gap-2 relative flex-[0_0_auto] rounded-lg border border-solid border-greygrey-400 cursor-pointer hover:bg-greygrey-50"
          onClick={handleNext}
          aria-label="Next page"
        >
          <span className="relative w-fit mt-[-1.00px] font-body-2-medium font-[number:var(--body-2-medium-font-weight)] text-greygrey-500 text-[length:var(--body-2-medium-font-size)] tracking-[var(--body-2-medium-letter-spacing)] leading-[var(--body-2-medium-line-height)] whitespace-nowrap [font-style:var(--body-2-medium-font-style)]">
            Tiếp
          </span>
          <img
            className="relative w-5 h-5"
            alt=""
            src="https://c.animaapp.com/iSVrHgil/img/chevron-right.svg"
            aria-hidden="true"
          />
        </button>
      </div>
    </nav>
  );
};
