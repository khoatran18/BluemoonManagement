// src/Layout/Apartment/Apartment.jsx
import React from 'react';
// Import các section con
import { SearchBarSection } from './sections/SearchBarSection/SearchBarSection';
import { DataTableSection } from './sections/DataTableSection/DataTableSection';
import { PaginationControlsSection } from './sections/PaginationControlsSection/PaginationControlsSection';
const Apartment = () => {
  return (
    <div className="apartment-container"> 
       {/* Header hoặc Tiêu đề nếu cần */}
       <h1>Căn hộ</h1>
       
       {/* Các thành phần con */}
       <SearchBarSection />
       <DataTableSection />
    </div>
  );
};

export default Apartment;