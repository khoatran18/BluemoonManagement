import React, { useState } from 'react';
import '../Apartment/ApartmentManagement.css';
import './FeeCollect.css';
import FilterSection from './section/FilterSection';
import { FeeCollectDataTableSection } from './section/DataTableSection.jsx';

const FEE_COLLECTION_TABLE_STATE_KEY = "fee-collection-table-state:v1";

function readFeeCollectionTableState() {
	try {
		const raw = sessionStorage.getItem(FEE_COLLECTION_TABLE_STATE_KEY);
		if (!raw) return null;
		const parsed = JSON.parse(raw);
		return parsed && typeof parsed === "object" ? parsed : null;
	} catch {
		return null;
	}
}

const FeeCollect = () => {
	const [searchQuery, setSearchQuery] = useState(() => {
		const saved = readFeeCollectionTableState();
		return typeof saved?.searchQuery === "string" ? saved.searchQuery : "";
	});

	console.log("ApartmentManagement searchQuery state:", searchQuery);

	return (
		<div className="apartment-management-container"> 
			 <FilterSection onSearchChange={setSearchQuery} />
			 <FeeCollectDataTableSection searchQuery={searchQuery} />
		</div>
	);
};

export default FeeCollect;

