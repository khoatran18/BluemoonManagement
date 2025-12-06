import React, { useState } from 'react';
import './ApartmentManagement.css';
import { ApartmentSearchBarSection } from './sections/SearchBarSection/ApartmentSearchBarSection.jsx';
import { ApartmentDataTableSection } from './sections/DataTableSection/ApartmentDataTableSection.jsx';

const ApartmentManagement = () => {
  const [searchQuery, setSearchQuery] = useState('');

  console.log("ApartmentManagement searchQuery state:", searchQuery);

  return (
    <div className="apartment-management-container"> 
       <ApartmentSearchBarSection onSearchChange={setSearchQuery} />
       <ApartmentDataTableSection searchQuery={searchQuery} />
    </div>
  );
};

export default ApartmentManagement;
