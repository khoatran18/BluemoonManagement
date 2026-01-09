import { useState, useRef, useEffect } from "react";
import filterIcon from "../../../assets/icon/fee/filter.svg";
import searchIcon from "../../../assets/icon/fee/search.svg";
import "./FilterSection.css";
import AddButton from "../../../Components/Button/AddButton";
import Button from "../../../Components/Button/Button";
import { DeleteFeeHistoriesModal } from "../../../Components/DeleteHistoriesModal/DeleteFeeHistoriesModal";
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
    onAddFee,
    onAddCategory,
    categoryRefreshToken
}) {
    const [open, setOpen] = useState(false);
    const containerRef = useRef(null);
    const [localSearch, setLocalSearch] = useState(search || '');
    const [isDeleteHistoryOpen, setIsDeleteHistoryOpen] = useState(false);

    useEffect(() => {
        setLocalSearch(search || '');
    }, [search]);

    useEffect(() => {
        const id = setTimeout(() => {
            if (typeof onSearch === 'function') onSearch(localSearch);
        }, 300);
        return () => clearTimeout(id);
    }, [localSearch]);

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
        if (typeof onChangeType === 'function') onChangeType([]);
        if (typeof onChangeStatus === 'function') onChangeStatus([]);
        if (typeof onChangeCategory === 'function') onChangeCategory('');
        if (typeof onChangeAmount === 'function') onChangeAmount('');
        if (typeof onChangeApplicableMonth === 'function') onChangeApplicableMonth('');
        if (typeof onChangeEffectiveDate === 'function') onChangeEffectiveDate('');
        if (typeof onChangeExpiryDate === 'function') onChangeExpiryDate('');
        if (typeof onSearch === 'function') onSearch('');
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

                    <div className="panel-title">Danh mục</div>
                    <CategorySelector active={activeCategory} onChange={onChangeCategory} refreshToken={categoryRefreshToken} />

                    <div className="panel-title">Số tiền</div>
                    <AmountFilter value={amount} onChange={onChangeAmount} />

                    <div className="panel-title">Thời gian</div>
                    <div className="time-filters">
                        <label>
                            Tháng áp dụng
                            <input type="month" value={applicableMonth || ''} onChange={(e) => onChangeApplicableMonth(e.target.value)} />
                        </label>
                        <label>
                            Ngày hiệu lực
                            <input
                                type="date"
                                value={effectiveDate || ''}
                                onChange={(e) => onChangeEffectiveDate(e.target.value)}
                                min={applicableMonth ? `${applicableMonth}-01` : undefined}
                                max={applicableMonth ? (() => { const [y, m] = applicableMonth.split('-'); const last = new Date(Number(y), Number(m), 0).getDate(); return `${y}-${m}-${String(last).padStart(2,'0')}` })() : undefined}
                            />
                        </label>
                        <label>
                            Ngày hết hạn
                            <input
                                type="date"
                                value={expiryDate || ''}
                                onChange={(e) => onChangeExpiryDate(e.target.value)}
                                min={effectiveDate ? (() => { const d = new Date(effectiveDate); d.setDate(d.getDate()+1); return d.toISOString().slice(0,10); })() : undefined}
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
                        active={activeStatus}
                        onChange={onChangeStatus}
                    />
                    <div className="filter-panel-actions">
                        <button type="button" className="btn-reset" onClick={handleReset}>Đặt lại</button>
                        <button type="button" className="btn-close" onClick={() => setOpen(false)}>Đóng</button>
                    </div>
                </div>
            )}

            <div className="filter-add-actions">
                <Button className="filter-add-btn" onClick={() => setIsDeleteHistoryOpen(true)} icon={null}>
                    LỊCH SỬ XÓA
                </Button>
                <AddButton
                    className="filter-add-btn filter-add-btn-category"
                    onClick={() => {
                        if (typeof onAddCategory === 'function') onAddCategory();
                    }}
                >
                    THÊM DANH MỤC
                </AddButton>

                <AddButton className="filter-add-btn" onClick={() => { if (typeof onAddFee === 'function') onAddFee(); else console.log('Add clicked'); }}>
                    THÊM PHÍ
                </AddButton>
            </div>

            <DeleteFeeHistoriesModal
                isOpen={isDeleteHistoryOpen}
                onClose={() => setIsDeleteHistoryOpen(false)}
            />
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

function CategorySelector({ active, onChange, refreshToken }) {
    const [categories, setCategories] = useState([]);

    useEffect(() => {
        (async () => {
            try {
                const resp = await getFeeCategories({ page: 1, limit: 1000 });
                setCategories(resp || []);
            } catch (err) {
                console.error('Failed to fetch categories', err);
            }
        })();
    }, [refreshToken]);

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
