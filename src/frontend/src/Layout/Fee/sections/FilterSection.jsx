import { useState, useRef, useEffect } from "react";
import filterIcon from "../../../assets/icon/fee/filter.svg";
import searchIcon from "../../../assets/icon/fee/search.svg";
import "./FilterSection.css";
import AddButton from "../../../Components/Button/AddButton";
import { getFeeCategories } from "../../../api/feeApi";

export default function FilterSection({
    activeType,
    activeStatus,
    search,
    activeCategory,
    amount,
    applicableMonth,
    effectiveDate,
    expiryDate,
    onChangeType,
    onChangeStatus,
    onSearch,
    onChangeCategory,
    onChangeAmount,
    onChangeApplicableMonth,
    onChangeEffectiveDate,
    onChangeExpiryDate,
    onAddFee
}) {

    const [open, setOpen] = useState(false);
    const containerRef = useRef(null);
    // Local filter state
    const [localSearch, setLocalSearch] = useState(search || '');
    const [localType, setLocalType] = useState(activeType || []);
    const [localStatus, setLocalStatus] = useState(activeStatus || []);
    const [localCategory, setLocalCategory] = useState(activeCategory || '');
    const [localAmount, setLocalAmount] = useState(amount || '');
    const [localApplicableMonth, setLocalApplicableMonth] = useState(applicableMonth || '');
    const [localEffectiveDate, setLocalEffectiveDate] = useState(effectiveDate || '');
    const [localExpiryDate, setLocalExpiryDate] = useState(expiryDate || '');

    useEffect(() => { setLocalSearch(search || ''); }, [search]);
    useEffect(() => { setLocalType(activeType || []); }, [activeType]);
    useEffect(() => { setLocalStatus(activeStatus || []); }, [activeStatus]);
    useEffect(() => { setLocalCategory(activeCategory || ''); }, [activeCategory]);
    useEffect(() => { setLocalAmount(amount || ''); }, [amount]);
    useEffect(() => { setLocalApplicableMonth(applicableMonth || ''); }, [applicableMonth]);
    useEffect(() => { setLocalEffectiveDate(effectiveDate || ''); }, [effectiveDate]);
    useEffect(() => { setLocalExpiryDate(expiryDate || ''); }, [expiryDate]);

    useEffect(() => {
        if (!open) return;
        const handleClickOutside = (e) => {
            if (containerRef.current && !containerRef.current.contains(e.target)) setOpen(false);
        };
        document.addEventListener("click", handleClickOutside);
        document.addEventListener("touchstart", handleClickOutside);
        return () => {
            document.removeEventListener("click", handleClickOutside);
            document.removeEventListener("touchstart", handleClickOutside);
        };
    }, [open]);

    const handleReset = () => {
        setLocalType([]);
        setLocalStatus([]);
        setLocalCategory('');
        setLocalAmount('');
        setLocalApplicableMonth('');
        setLocalEffectiveDate('');
        setLocalExpiryDate('');
        setLocalSearch('');
    };

    const handleConfirm = () => {
        if (typeof onChangeType === 'function') onChangeType(localType);
        if (typeof onChangeStatus === 'function') onChangeStatus(localStatus);
        if (typeof onChangeCategory === 'function') onChangeCategory(localCategory);
        if (typeof onChangeAmount === 'function') onChangeAmount(localAmount);
        if (typeof onChangeApplicableMonth === 'function') onChangeApplicableMonth(localApplicableMonth);
        if (typeof onChangeEffectiveDate === 'function') onChangeEffectiveDate(localEffectiveDate);
        if (typeof onChangeExpiryDate === 'function') onChangeExpiryDate(localExpiryDate);
        if (typeof onSearch === 'function') onSearch(localSearch);
        setOpen(false);
    };

    return (
        <div className="filter-bar" ref={containerRef}>

            <button
                className="filter-btn"
                onClick={() => setOpen(prev => !prev)}
            >
                <img src={filterIcon} alt="Filter" />
                <span>Lọc</span>
            </button>

            <div className="search-box">
                <img src={searchIcon} alt="Search" className="search-icon" />
                <input
                    type="text"
                    placeholder="Tìm kiếm khoản phí"
                    value={typeof localSearch !== 'undefined' ? localSearch : search}
                    onChange={(e) => setLocalSearch(e.target.value)}
                />
            </div>

            {open && (
                <div className="filter-panel filter-panel-scrollable">
                    <div className="panel-title">Loại phí</div>
                    <FilterGroup
                        options={[
                            { key: "obligatory", label: "Định kỳ" },
                            { key: "impromptu", label: "Đột xuất" },
                            { key: "voluntary", label: "Tự nguyện" }
                        ]}
                        active={localType}
                        onChange={setLocalType}
                    />

                    <div className="panel-title">Danh mục</div>
                    <CategorySelector active={localCategory} onChange={setLocalCategory} />

                    <div className="panel-title">Số tiền</div>
                    <AmountFilter value={localAmount} onChange={setLocalAmount} />

                    <div className="panel-title">Thời gian</div>
                    <div className="time-filters">
                        <label>
                            Tháng áp dụng
                            <input type="month" value={localApplicableMonth || ''} onChange={(e) => setLocalApplicableMonth(e.target.value)} />
                        </label>
                        <label>
                            Ngày hiệu lực
                            <input
                                type="date"
                                value={localEffectiveDate || ''}
                                onChange={(e) => setLocalEffectiveDate(e.target.value)}
                                min={localApplicableMonth ? `${localApplicableMonth}-01` : undefined}
                                max={localApplicableMonth ? (() => { const [y, m] = localApplicableMonth.split('-'); const last = new Date(Number(y), Number(m), 0).getDate(); return `${y}-${m}-${String(last).padStart(2,'0')}` })() : undefined}
                            />
                        </label>
                        <label>
                            Ngày hết hạn
                            <input
                                type="date"
                                value={localExpiryDate || ''}
                                onChange={(e) => setLocalExpiryDate(e.target.value)}
                                min={localEffectiveDate ? (() => { const d = new Date(localEffectiveDate); d.setDate(d.getDate()+1); return d.toISOString().slice(0,10); })() : undefined}
                            />
                        </label>
                    </div>

                    <div className="panel-title">Trạng thái</div>
                    <FilterGroup
                        options={[
                            { key: "DRAFT", label: "Nháp" },
                            { key: "ACTIVE", label: "Đang hoạt động" },
                            { key: "CLOSED", label: "Đã đóng" },
                            { key: "ARCHIVED", label: "Lưu trữ" }
                        ]}
                        active={localStatus}
                        onChange={setLocalStatus}
                    />
                    <div className="filter-panel-actions">
                        <button type="button" className="btn-reset" onClick={handleReset}>Đặt lại</button>
                        <button type="button" className="btn-close" onClick={() => setOpen(false)}>Đóng</button>
                        <button type="button" className="btn-confirm" onClick={handleConfirm}>Xác nhận</button>
                    </div>
                </div>
            )}

            <AddButton className="filter-add-btn" onClick={() => { if (typeof onAddFee === 'function') onAddFee(); else console.log('Add clicked'); }}>
                THÊM PHÍ
            </AddButton>
        </div>
    );
}

function FilterGroup({ options, active = [], onChange }) {
    const toggle = (key) => {
        let newList;
        if (active.includes(key)) newList = active.filter(item => item !== key);
        else newList = [...active, key];
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

function CategorySelector({ active, onChange }) {
    const [categories, setCategories] = useState([]);

    useEffect(() => {
        (async () => {
            try {
                const resp = await getFeeCategories();
                setCategories(resp || []);
            } catch (err) {
                console.error('Failed to fetch categories', err);
            }
        })();
    }, []);

    return (
        <select value={active || ''} onChange={(e) => onChange(e.target.value)}>
            <option value="">Tất cả</option>
            {categories.map(c => (
                <option key={c.fee_category_id || c.id} value={c.fee_category_id || c.id}>{c.name || c.title}</option>
            ))}
        </select>
    );
}

function AmountFilter({ value, onChange }) {
    const [local, setLocal] = useState(value || '');
    useEffect(() => setLocal(value || ''), [value]);
    const handle = (e) => {
        const digits = (e.target.value || '').replace(/\D/g, '');
        setLocal(digits);
        onChange(digits ? Number(digits) : '');
    };
    return (
        <div>
            <input type="text" inputMode="numeric" value={local} onChange={handle} placeholder="Số tiền (VND)" />
        </div>
    );
}
