import React, { useState } from 'react';
// Import các section con
import './ResidentManagement.css';
import FilterSection from './sections/FilterSection';
import { DataTableSection } from './sections/DataTableSection/DataTableSection';
import { useToasts } from '../../Components/Toast/ToastContext';
import { usePersistentState } from '../../hooks/usePersistentState';

const ResidentManagement = () => {
  const [searchQuery, setSearchQuery] = usePersistentState('resident.searchQuery', '');
  const [apartmentId, setApartmentId] = usePersistentState('resident.apartmentId', '');
  const [phoneNumber, setPhoneNumber] = usePersistentState('resident.phoneNumber', '');
  const [email, setEmail] = usePersistentState('resident.email', '');
  const [refreshKey, setRefreshKey] = useState(0);

  console.log("ResidentManagement searchQuery state:", searchQuery);

  const { addToast } = useToasts();
  const showToast = (message, variant = 'success', duration = 4000) => {
      if (typeof addToast === 'function') addToast({ message, variant, duration });
  };

  return (
    <div className="resident-management-container"> 
       <FilterSection
          search={searchQuery}
          apartmentId={apartmentId}
          phoneNumber={phoneNumber}
          email={email}
          onSearchChange={setSearchQuery}
          onApartmentIdChange={setApartmentId}
          onPhoneNumberChange={setPhoneNumber}
          onEmailChange={setEmail}
          onRefresh={() => setRefreshKey((k) => k + 1)}
          onNotify={({message, variant = 'success', duration = 3000}) => showToast(message, variant, duration)}
        />
       <DataTableSection 
          searchQuery={searchQuery}
          apartmentId={apartmentId}
          phoneNumber={phoneNumber}
          email={email}
          refreshKey={refreshKey}
          onNotify={({message, variant = 'success', duration = 3000}) => showToast(message, variant, duration)}
        />
    </div>
  );
};

export default ResidentManagement;
