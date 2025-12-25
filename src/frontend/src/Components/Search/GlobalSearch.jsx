import React from 'react';
import './GlobalSearch.css';

const GlobalSearch = () => {
  return (
    <div className="global-search-container">
      {/* Icon search (SVG đơn giản) */}
      <svg xmlns="http://www.w3.org/2000/svg" className="search-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
      </svg>
      <input type="text" placeholder="Tìm kiếm gì đó..." className="search-input" />
    </div>
  );
};

export default GlobalSearch;