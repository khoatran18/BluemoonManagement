// src/Layout/Apartment/Apartment.jsx
import React, { useState } from 'react';
// Import các section con
import { SearchBarSection } from './sections/SearchBarSection/SearchBarSection';
import { DataTableSection } from './sections/DataTableSection/DataTableSection';
import { PaginationControlsSection } from './sections/PaginationControlsSection/PaginationControlsSection';

const Apartment = () => {
  const [searchQuery, setSearchQuery] = useState('');

  console.log("Apartment searchQuery state:", searchQuery);

  return (
    <div className="apartment-container"> 
       <SearchBarSection onSearchChange={setSearchQuery} />
       <DataTableSection searchQuery={searchQuery} />
    </div>
  );
};

export default Apartment;