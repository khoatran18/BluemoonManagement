import './Fee.css'
import { useState } from "react"
import FilterSection from "./sections/FilterSection"
import DataTableSection from "./sections/DataTableSection"

export default function Fee(){
    const [activeType, setActiveType] = useState([]);
    const [activeStatus, setActiveStatus] = useState([]);
    const [search, setSearch] = useState("");
    return (
        <div className="fee-container">
                <FilterSection
                    activeType={activeType}
                    activeStatus={activeStatus}
                    search={search}
                    onChangeType={setActiveType}
                    onChangeStatus={setActiveStatus}
                    onSearch={setSearch}
                />

                <DataTableSection 
                    activeType={activeType}
                    activeStatus={activeStatus}
                    search={search}   
                />
        </div>
    )
}