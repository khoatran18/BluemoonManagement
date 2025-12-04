import React, { useState } from 'react';
import '../ApartmentManagement/ApartmentManagement.css';
import { ApartmentSearchBarSection } from './sections/ApartmentSearchBarSection/ApartmentSearchBarSection';
import { ApartmentDataTableSection } from './sections/ApartmentDataTableSection/ApartmentDataTableSection';

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
