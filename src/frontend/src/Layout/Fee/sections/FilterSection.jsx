import { useState, useRef, useEffect} from "react";
import filterIcon from "../../../assets/icon/fee/filter.svg";
import searchIcon from "../../../assets/icon/fee/search.svg";
import "./FilterSection.css";

export default function FilterSection({
  activeType,
  activeStatus,
  search,
  onChangeType,
  onChangeStatus,
  onSearch
}) {

    const [open, setOpen] = useState(false);

    return (
        <div className="filter-bar">

        <button 
            className="filter-btn" 
            onClick={(e) => {
                setOpen((prev) => !prev);
            }}>
            <img src={filterIcon} alt="Filter" />
            <span>Lọc</span>
        </button>

        <div className="search-box">
            <img src={searchIcon} alt="Search" className="search-icon" />
            <input
            type="text"
            placeholder="Tìm kiếm khoản phí"
            value={search}
            onChange={(e) => onSearch(e.target.value)}
            />
        </div>

        {open && (
            <div className="filter-panel">
            <div className="panel-title">Loại phí</div>
            <FilterGroup
                options={[
                { key: "obligatory", label: "Định kỳ" },
                { key: "impromptu", label: "Đột xuất" },
                { key: "voluntary", label: "Tự nguyện" }
                ]}
                active={activeType}
                onChange={onChangeType}
            />

            <div className="panel-title">Trạng thái</div>
            <FilterGroup
                options={[
                { key: "draft", label: "Nháp" },
                { key: "active", label: "Đang hoạt động" },
                { key: "closed", label: "Đã đóng" },
                { key: "archived", label: "Lưu trữ" }
                ]}
                active={activeStatus}
                onChange={onChangeStatus}
            />
            </div>
        )}
        </div>
    );
}

function FilterGroup({ options, active = [], onChange }) {
    const toggle = (key) => {
        let newList;

        if (active.includes(key)) {
    
        newList = active.filter(item => item !== key);
        } else {
        
        newList = [...active, key];
        }

        onChange(newList);
    };

    return (
        <div className="filter-group">
        {options.map(o => (
            <label className="filter-option" key={o.key}>
            <input
                type="checkbox"
                checked={active.includes(o.key)}
                onChange={() => toggle(o.key)}
            />
            <span>{o.label}</span>
            </label>
        ))}
        </div>
    );
}
