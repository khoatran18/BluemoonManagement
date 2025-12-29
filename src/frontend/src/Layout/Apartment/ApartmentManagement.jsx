import React, { useState } from 'react';
import './ApartmentManagement.css';
import FilterSection from './sections/FilterSection';
import { ApartmentDataTableSection } from './sections/DataTableSection/ApartmentDataTableSection.jsx';

const ApartmentManagement = () => {
  const [searchQuery, setSearchQuery] = useState('');

  console.log("ApartmentManagement searchQuery state:", searchQuery);

  return (
    <div className="apartment-management-container"> 
       <FilterSection onSearchChange={setSearchQuery} />
       <ApartmentDataTableSection searchQuery={searchQuery} />
    </div>
  );
};

export default ApartmentManagement;
