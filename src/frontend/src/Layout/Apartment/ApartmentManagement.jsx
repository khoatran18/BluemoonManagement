import React, { useState } from 'react';
import './ApartmentManagement.css';
import FilterSection from './sections/FilterSection';
import { ApartmentDataTableSection } from './sections/DataTableSection/ApartmentDataTableSection.jsx';
import { useToasts } from '../../Components/Toast/ToastContext';
import { usePersistentState } from '../../hooks/usePersistentState';

const ApartmentManagement = () => {
  const [searchQuery, setSearchQuery] = usePersistentState('apartment.searchQuery', '');
  const [refreshKey, setRefreshKey] = useState(0);

  console.log("ApartmentManagement searchQuery state:", searchQuery);

  const { addToast } = useToasts();
  const showToast = (message, variant = 'success', duration = 4000) => {
    if (typeof addToast === 'function') addToast({ message, variant, duration });
  };

  return (
    <div className="apartment-management-container"> 
       <FilterSection
         search={searchQuery}
         onSearchChange={setSearchQuery}
         onNotify={({ message, variant = 'success', duration = 3000 }) => showToast(message, variant, duration)}
         onRefresh={() => setRefreshKey((k) => k + 1)}
       />
       <ApartmentDataTableSection
         searchQuery={searchQuery}
         refreshKey={refreshKey}
         onNotify={({message, variant = 'success', duration = 3000}) => showToast(message, variant, duration)}
       />
    </div>
  );
};

export default ApartmentManagement;
