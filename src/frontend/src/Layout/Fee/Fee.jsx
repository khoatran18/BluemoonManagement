import './Fee.css'
import { useState, useRef } from "react"
import FilterSection from "./sections/FilterSection"
import DataTableSection from "./sections/DataTableSection"
import AddFeeForm from "../../Components/AddFeeForm/AddFeeForm";
import AddFeeCategoryForm from "../../Components/AddFeeCategoryForm/AddFeeCategoryForm";
import { Modal } from "../../Components/Modal/Modal";
import { useToasts } from "../../Components/Toast/ToastContext";
import { createFee, updateFee, getFeeDetails, getFeeTypes, getFeeCategories, createFeeCategory } from "../../api/feeApi";

export default function Fee(){
    const [activeType, setActiveType] = useState([]);
    const [activeStatus, setActiveStatus] = useState([]);
    const [search, setSearch] = useState("");
    const [activeCategory, setActiveCategory] = useState("");
    const [filterAmount, setFilterAmount] = useState("");
    const [filterApplicableMonth, setFilterApplicableMonth] = useState("");
    const [filterEffectiveDate, setFilterEffectiveDate] = useState("");
    const [filterExpiryDate, setFilterExpiryDate] = useState("");
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isCategoryModalOpen, setIsCategoryModalOpen] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [editingId, setEditingId] = useState(null);
    const [initialData, setInitialData] = useState({});
    const [feeTypes, setFeeTypes] = useState([]);
    const [feeCategories, setFeeCategories] = useState([]);
    const [categoryRefreshToken, setCategoryRefreshToken] = useState(0);

    const refreshRef = useRef(null);

    const registerRefresh = (fn) => {
        refreshRef.current = fn;
    };

    const openAdd = async () => {
        setIsEditing(false);
        setEditingId(null);
        // load types/categories before opening
        try {
            const typesResp = await getFeeTypes();
            const catsResp = await getFeeCategories({ page: 1, limit: 1000 });
            setFeeTypes(typesResp || []);
            setFeeCategories(catsResp || []);
        } catch (err) {
            console.error('Failed to load fee types/categories', err);
        }
        setInitialData({});
        setIsModalOpen(true);
    };

    const openAddCategory = async () => {
        setIsCategoryModalOpen(true);
    };

    const openEdit = async (feeId) => {
        setIsEditing(true);
        setEditingId(feeId);
        try {
            const typesResp = await getFeeTypes();
            const catsResp = await getFeeCategories({ page: 1, limit: 1000 });
            setFeeTypes(typesResp || []);
            setFeeCategories(catsResp || []);

            const resp = await getFeeDetails(feeId);
            let payload = resp;
            if (payload && payload.data) payload = payload.data;
            if (payload && payload.data) payload = payload.data;
            setInitialData(payload || {});
        } catch (err) {
            console.error('Failed to load fee for edit', err);
        }
        setIsModalOpen(true);
    };

    const handleSubmit = async (form) => {
        try {
            if (isEditing && editingId) {
                await updateFee(editingId, form);
                showToast('Cập nhật phí thành công', 'success');
            } else {
                await createFee(form);
                showToast('Tạo phí thành công', 'success');
            }
            setIsModalOpen(false);
            if (refreshRef.current) await refreshRef.current();
        } catch (err) {
            console.error('Save fee error', err);
            showToast(err?.message || 'Lưu phí thất bại', 'error');
        }
    };

    const handleCreateCategory = async (form) => {
        try {
            await createFeeCategory(form);
            showToast('Tạo danh mục phí thành công', 'success');
            setIsCategoryModalOpen(false);
            setCategoryRefreshToken((v) => v + 1);

            // Keep fee form dropdowns fresh if user opens Add Fee next.
            try {
                const catsResp = await getFeeCategories({ page: 1, limit: 1000 });
                setFeeCategories(catsResp || []);
            } catch {
                // ignore
            }
        } catch (err) {
            console.error('Create fee category error', err);
            showToast(err?.message || 'Tạo danh mục phí thất bại', 'error');
        }
    };

    const { addToast } = useToasts();
    const showToast = (message, variant = 'success', duration = 4000) => {
        if (typeof addToast === 'function') addToast({ message, variant, duration });
    };
    return (
        <div className="fee-container">
            <FilterSection
                activeType={activeType}
                activeStatus={activeStatus}
                search={search}
                activeCategory={activeCategory}
                amount={filterAmount}
                applicableMonth={filterApplicableMonth}
                effectiveDate={filterEffectiveDate}
                expiryDate={filterExpiryDate}
                onChangeType={setActiveType}
                onChangeStatus={setActiveStatus}
                onSearch={setSearch}
                onChangeCategory={setActiveCategory}
                onChangeAmount={setFilterAmount}
                onChangeApplicableMonth={setFilterApplicableMonth}
                onChangeEffectiveDate={setFilterEffectiveDate}
                onChangeExpiryDate={setFilterExpiryDate}
                onAddFee={openAdd}
                onAddCategory={openAddCategory}
                categoryRefreshToken={categoryRefreshToken}
            />

            <DataTableSection 
                activeType={activeType}
                activeStatus={activeStatus}
                fee_category_id={activeCategory}
                fee_amount={filterAmount}
                applicable_month={filterApplicableMonth}
                effective_date={filterEffectiveDate}
                expiry_date={filterExpiryDate}
                search={search}
                onEditRequest={openEdit}
                registerRefresh={registerRefresh}
                onNotify={({message, variant = 'success', duration = 3000}) => showToast(message, variant, duration)}
            />

            <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title={isEditing ? 'Cập nhật phí' : 'Thêm phí mới'}>
                <AddFeeForm initial={initialData} feeTypes={feeTypes} feeCategories={feeCategories} isEditing={isEditing} onCancel={() => setIsModalOpen(false)} onSubmit={handleSubmit} />
            </Modal>

            <Modal isOpen={isCategoryModalOpen} onClose={() => setIsCategoryModalOpen(false)} title={'Thêm danh mục phí'}>
                <AddFeeCategoryForm
                    onCancel={() => setIsCategoryModalOpen(false)}
                    onSubmit={handleCreateCategory}
                />
            </Modal>
            {/* Toasts rendered by ToastProvider */}
        </div>
    )
}