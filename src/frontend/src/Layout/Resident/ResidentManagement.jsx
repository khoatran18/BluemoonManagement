import React, { useState } from 'react';
import './ResidentManagement.css';
import { SearchBarSection } from './sections/SearchBarSection/SearchBarSection';
import { DataTableSection } from './sections/DataTableSection/DataTableSection';

const ResidentManagement = () => {
  const [searchQuery, setSearchQuery] = useState('');

  console.log("ResidentManagement searchQuery state:", searchQuery);

  return (
    <div className="resident-management-container"> 
       <SearchBarSection onSearchChange={setSearchQuery} />
       <DataTableSection searchQuery={searchQuery} />
    </div>
  );
};

export default ResidentManagement;
