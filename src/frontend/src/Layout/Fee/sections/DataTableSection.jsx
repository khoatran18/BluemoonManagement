import { useState, useEffect } from "react";
import { ActionMenu } from "../../../Components/ActionMenu";
import Table from "../../../Components/Table/Table";
import Column from "../../../Components/Table/Column";
import Tag from "../../../Components/Tag/Tag";
import LoadingSpinner from "../../../Components/LoadingSpinner/LoadingSpinner";
import { getFees, getFeeDetails, deleteFee, updateFee } from "../../../api/feeApi";
import { createAdjustment, deleteAdjustment, getAdjustments, updateAdjustment } from "../../../api/adjustmentApi";
import { DetailFeeModal } from "../../../Components/DetailFeeModal/DetailFeeModal";
import { DeleteConfirmModal } from "../../../Components/DeleteConfirmModal";
import { AdjustmentModal } from "../../../Components/AdjustmentModal/AdjustmentModal";
import { FeeAdjustmentsModal } from "../../../Components/FeeAdjustmentsModal/FeeAdjustmentsModal";
import { OBLIGATORY_FEE_TYPE_ID, VOLUNTARY_FEE_TYPE_ID, IMPROMPTU_FEE_TYPE_ID } from "../../../constants/feeTypeIds";
import "../Fee.css";

const typeMap = {
  obligatory: "Định kỳ",
  voluntary: "Tự nguyện",
  impromptu: "Đột xuất"
};

const statusMap = {
  draft: "Nháp",
  active: "Đang hoạt động",
  closed: "Đã đóng",
  archived: "Lưu trữ"
};


export default function DataTableSection({ activeType, activeStatus, fee_category_id, fee_amount, applicable_month, effective_date, expiry_date, search, onEditRequest, registerRefresh, onNotify }) {
  const typeIdMap = {
    obligatory: Number(OBLIGATORY_FEE_TYPE_ID),
    voluntary: Number(VOLUNTARY_FEE_TYPE_ID),
    impromptu: Number(IMPROMPTU_FEE_TYPE_ID)
  };

  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [totalItems, setTotalItems] = useState(0);

  const [detailLoading, setDetailLoading] = useState(false);
  const [isDetailOpen, setIsDetailOpen] = useState(false);
  const [selectedFee, setSelectedFee] = useState(null);
  const [isDeleteOpen, setIsDeleteOpen] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState(null);

  const [isFeeAdjustmentsOpen, setIsFeeAdjustmentsOpen] = useState(false);
  const [feeAdjustmentsFee, setFeeAdjustmentsFee] = useState(null);
  const [feeAdjustmentsLoading, setFeeAdjustmentsLoading] = useState(false);
  const [feeAdjustments, setFeeAdjustments] = useState([]);

  const [isAdjustmentModalOpen, setIsAdjustmentModalOpen] = useState(false);
  const [adjustmentModalLoading, setAdjustmentModalLoading] = useState(false);
  const [adjustmentModalFeeId, setAdjustmentModalFeeId] = useState(null);
  const [editingAdjustment, setEditingAdjustment] = useState(null);
  const [adjustmentModalFeeRange, setAdjustmentModalFeeRange] = useState({ effective_date: null, expiry_date: null });

  const [isDeleteAdjustmentOpen, setIsDeleteAdjustmentOpen] = useState(false);
  const [deleteAdjustmentTarget, setDeleteAdjustmentTarget] = useState(null);

  const [page, setPage] = useState(1);
  const [limit, setLimit] = useState(5);

  const fetchFees = async () => {
    setLoading(true);
    setError(null);
    try {
      const params = { page, limit };

      if (activeType && activeType.length > 0) {
        const typeIds = activeType.map(t => typeIdMap[t.toLowerCase()]).filter(Boolean);
        if (typeIds.length === 1) params.fee_type_id = typeIds[0];
        else if (typeIds.length > 1) params.fee_type_id = typeIds;
      }

      if (activeStatus && activeStatus.length > 0) {
        params.status = activeStatus;
      }

      if (fee_category_id) params.fee_category_id = fee_category_id;
      if (fee_amount) params.fee_amount = Number(fee_amount);
      if (applicable_month) params.applicable_month = applicable_month;
      if (effective_date) params.effective_date = effective_date;
      if (expiry_date) params.expiry_date = expiry_date;

      if (search && search.trim() !== '') params.fee_name = search.trim();

      const response = await getFees(params);
      setData((response.fees || []).map(fee => ({
        id: fee.fee_id,
        name: fee.fee_name,
        type: Object.keys(typeIdMap).find(key => typeIdMap[key] === fee.fee_type_id) || 'unknown',
        value: (fee.fee_amount || 0).toLocaleString("vi-VN") + "đ",
        status: fee.status
      })));
      setTotalItems(response.total_items || 0);
    } catch (err) {
      onNotify({ message: `Lấy phí thất bại\n${err.message}`, variant: 'error', duration: 4000 })
      setData([]);
    } finally {
      setLoading(false);
    }
  };

  const fetchAdjustmentsForFee = async (feeId) => {
    if (!feeId) return;
    setFeeAdjustmentsLoading(true);
    try {
      const resp = await getAdjustments({ fee_id: feeId, page: 1, limit: 100 });
      const list = resp?.adjustments || resp?.items || [];
      setFeeAdjustments(Array.isArray(list) ? list : []);
    } catch (err) {
      setFeeAdjustments([]);
      if (typeof onNotify === "function") {
        onNotify({ message: `Lấy điều chỉnh thất bại\n${err.message}`, variant: "error", duration: 4000 });
      }
    } finally {
      setFeeAdjustmentsLoading(false);
    }
  };

  const openAdjustmentsModal = async (fee) => {
    if (!fee?.id) return;

    let detail = null;
    try {
      const resp = await getFeeDetails(fee.id);
      detail = resp;
      if (detail && detail.data) detail = detail.data;
      if (detail && detail.data) detail = detail.data;
    } catch (err) {
      detail = null;
    }

    setFeeAdjustmentsFee({
      fee_id: fee.id,
      fee_name: fee.name,
      status: fee.status,
      effective_date: detail?.effective_date || null,
      expiry_date: detail?.expiry_date || null,
    });
    setIsFeeAdjustmentsOpen(true);
    await fetchAdjustmentsForFee(fee.id);
  };

  const openCreateAdjustment = (feeId) => {
    setAdjustmentModalFeeId(feeId);
    setEditingAdjustment(null);
    if (feeAdjustmentsFee?.fee_id && String(feeAdjustmentsFee.fee_id) === String(feeId)) {
      setAdjustmentModalFeeRange({
        effective_date: feeAdjustmentsFee?.effective_date || null,
        expiry_date: feeAdjustmentsFee?.expiry_date || null,
      });
    } else {
      setAdjustmentModalFeeRange({ effective_date: null, expiry_date: null });
    }
    setIsAdjustmentModalOpen(true);
  };

  const openEditAdjustment = (feeId, adj) => {
    setAdjustmentModalFeeId(feeId);
    setEditingAdjustment(adj);
    if (feeAdjustmentsFee?.fee_id && String(feeAdjustmentsFee.fee_id) === String(feeId)) {
      setAdjustmentModalFeeRange({
        effective_date: feeAdjustmentsFee?.effective_date || null,
        expiry_date: feeAdjustmentsFee?.expiry_date || null,
      });
    } else {
      setAdjustmentModalFeeRange({ effective_date: null, expiry_date: null });
    }
    setIsAdjustmentModalOpen(true);
  };

  const handleSubmitAdjustment = async (payload) => {
    if (!adjustmentModalFeeId) return;
    setAdjustmentModalLoading(true);
    try {
      if (editingAdjustment?.adjustment_id) {
        await updateAdjustment(editingAdjustment.adjustment_id, payload);
        if (typeof onNotify === "function") onNotify({ message: "Cập nhật điều chỉnh thành công", variant: "success", duration: 3000 });
      } else {
        await createAdjustment(payload);
        if (typeof onNotify === "function") onNotify({ message: "Tạo điều chỉnh thành công", variant: "success", duration: 3000 });
      }
      await fetchAdjustmentsForFee(adjustmentModalFeeId);
      setIsAdjustmentModalOpen(false);
      setEditingAdjustment(null);
    } catch (err) {
      if (typeof onNotify === "function") onNotify({ message: `Lưu điều chỉnh thất bại\n${err.message}`, variant: "error", duration: 4000 });
    } finally {
      setAdjustmentModalLoading(false);
    }
  };

  const handleConfirmDeleteAdjustment = async (adjustmentId) => {
    try {
      await deleteAdjustment(adjustmentId);
      if (typeof onNotify === "function") onNotify({ message: "Xóa điều chỉnh thành công", variant: "success", duration: 3000 });
      if (feeAdjustmentsFee?.fee_id) {
        await fetchAdjustmentsForFee(feeAdjustmentsFee.fee_id);
      }
    } catch (err) {
      if (typeof onNotify === "function") onNotify({ message: `Xóa điều chỉnh thất bại\n${err.message}`, variant: "error", duration: 4000 });
    }
  };

  useEffect(() => {
    fetchFees();
    if (typeof registerRefresh === "function") registerRefresh(fetchFees);
  }, [page, limit, activeType, activeStatus, search, fee_category_id, fee_amount, applicable_month, effective_date, expiry_date]);

  useEffect(() => {
    setPage(1);
  }, [activeType, activeStatus, search, fee_category_id, fee_amount, applicable_month, effective_date, expiry_date]);

  const handleView = (row) => {
    setIsDetailOpen(true);
    setSelectedFee(null);
    setDetailLoading(true);

    (async () => {
      try {
        const resp = await getFeeDetails(row);

        let payload = resp;
        if (payload && payload.data) payload = payload.data;
        if (payload && payload.data) payload = payload.data;

        setSelectedFee(payload);
      } catch (err) {
        console.error('Error fetching fee detail', err);
      } finally {
        setDetailLoading(false);
      }
    })();
  }

  const handleEdit = (row) => {
    if (typeof onEditRequest === "function") {
      onEditRequest(row);
      return;
    }

    console.log("Edit:", row);
  };

  const handleDelete = async (row) => {
    if (!row) return;
    setDeleteTarget({ id: row, name: null });
    setIsDeleteOpen(true);
  };

  const handleConfirmDelete = async (id) => {
    try {
      await deleteFee(id);
      await fetchFees();
      if (typeof onNotify === 'function') onNotify({ message: 'Xóa phí thành công', variant: 'success', duration: 3000 });
    } catch (err) {
      console.error('Delete fee error', err);
      if (typeof onNotify === 'function') onNotify({ message: 'Xóa phí thất bại', variant: 'error', duration: 4000 });
    }
  };

  const handleChangeFeeStatus = async (feeId, nextStatus) => {
    if (!feeId || !nextStatus) return;
    try {
      const resp = await getFeeDetails(feeId);
      let details = resp;
      if (details && details.data) details = details.data;
      if (details && details.data) details = details.data;

      const current = (details?.status || details?.fee_status || "").toString().toUpperCase();
      const desired = nextStatus.toString().toUpperCase();

      if (current && current === desired) {
        if (typeof onNotify === "function") {
          onNotify({ message: "Trạng thái đã là giá trị này", variant: "info", duration: 2500 });
        }
        return;
      }

      const payload = {
        fee_type_id: details?.fee_type_id,
        fee_category_id: details?.fee_category_id,
        fee_name: details?.fee_name,
        fee_description: details?.fee_description,
        fee_amount: details?.fee_amount,
        applicable_month: details?.applicable_month,
        effective_date: details?.effective_date,
        expiry_date: details?.expiry_date,
        status: desired,
      };

      await updateFee(feeId, payload);
      await fetchFees();
      if (typeof onNotify === "function") {
        onNotify({ message: "Cập nhật trạng thái phí thành công", variant: "success", duration: 3000 });
      }
    } catch (err) {
      if (typeof onNotify === "function") {
        onNotify({ message: `Cập nhật trạng thái thất bại\n${err.message}`, variant: "error", duration: 4000 });
      }
    }
  };

  return (
    <div className="fee-data-table">
      {loading ? (
        <LoadingSpinner />
      ) : (
        <Table
          data={data}
          total={totalItems}
          page={page}
          limit={limit}
          onPageChange={setPage}
          onLimitChange={(l) => {
            setLimit(l);
            setPage(1);
          }}
        >
          <Column dataIndex="id" title="Mã phí" sortable key="id" />
          <Column
            dataIndex="name"
            title="Tên khoản phí"
            sortable
            key="name"
            className="fee-name-column"
            render={(name, record) => {
              return (
                <div>
                  <div className="fee-name-text" title={name}>{name}</div>

                  <button
                    type="button"
                    className="fee-adjustments-toggle"
                    onClick={(e) => {
                      e.stopPropagation();
                      openAdjustmentsModal({ id: record?.id, name, status: record?.status });
                    }}
                  >
                    Xem điều chỉnh
                  </button>
                </div>
              );
            }}
          />
          <Column
            dataIndex="type"
            title="Loại phí"
            key="type"
            render={(type) => (
              <Tag variant="Fee" type={type}>
                {typeMap[type] || "Khác"}
              </Tag>
            )}
          />
          <Column dataIndex="value" title="Số tiền" sortable key="value" />
          <Column
            dataIndex="status"
            title="Trạng thái"
            key="status"
            render={(status) => (
              <Tag variant="Status" status={(status || "").toLowerCase()}>
                {statusMap[(status || "").toLowerCase()] || status || ""}
              </Tag>
            )}
          />
          <Column
            dataIndex="actions"
            title="Thao tác"
            key="actions"
            render={(_, record) => (
              <div className="fee-actions-cell">
                {String(record?.status || "").toLowerCase() !== "closed" && (
                  <button
                    type="button"
                    className="fee-close-btn"
                    onClick={(e) => {
                      e.stopPropagation();
                      handleChangeFeeStatus(record.id, "CLOSED");
                    }}
                  >
                    Đóng phí
                  </button>
                )}

                <ActionMenu
                  recordId={record.id}
                  actions={[
                    {
                      label: "Xem",
                      icon: "view",
                      type: "view",
                      onClick: () => handleView(record.id)
                    },
                    {
                      label: "Sửa",
                      icon: "edit",
                      type: "edit",
                      onClick: () => handleEdit(record.id),
                    },
                    {
                      label: "Xóa",
                      icon: "delete",
                      type: "delete",
                      onClick: () => {
                        setDeleteTarget({ id: record.id, name: record.name });
                        setIsDeleteOpen(true);
                      },
                    },
                  ]}
                />
              </div>
            )}
          />
        </Table>
      )}

      {error && <p>Error loading fees: {error}</p>}

      <DetailFeeModal isOpen={isDetailOpen} onClose={() => setIsDetailOpen(false)} fee={selectedFee} loading={detailLoading} />
      <DeleteConfirmModal
        isOpen={isDeleteOpen}
        onClose={() => { setIsDeleteOpen(false); setDeleteTarget(null); }}
        data={deleteTarget}
        title="phí"
        onConfirm={(id) => handleConfirmDelete(id)}
      />

      <AdjustmentModal
        isOpen={isAdjustmentModalOpen}
        onClose={() => {
          setIsAdjustmentModalOpen(false);
          setEditingAdjustment(null);
          setAdjustmentModalFeeRange({ effective_date: null, expiry_date: null });
        }}
        feeId={adjustmentModalFeeId}
        feeEffectiveDate={adjustmentModalFeeRange?.effective_date}
        feeExpiryDate={adjustmentModalFeeRange?.expiry_date}
        initialAdjustment={editingAdjustment}
        loading={adjustmentModalLoading}
        onSubmit={handleSubmitAdjustment}
      />

      <FeeAdjustmentsModal
        isOpen={isFeeAdjustmentsOpen}
        onClose={() => setIsFeeAdjustmentsOpen(false)}
        fee={feeAdjustmentsFee}
        adjustments={feeAdjustments}
        loading={feeAdjustmentsLoading}
        onAdd={() => {
          if (!feeAdjustmentsFee?.fee_id) return;
          openCreateAdjustment(feeAdjustmentsFee.fee_id);
        }}
        onEdit={(adj) => {
          if (!feeAdjustmentsFee?.fee_id) return;
          openEditAdjustment(feeAdjustmentsFee.fee_id, adj);
        }}
        onDelete={(adj) => {
          setDeleteAdjustmentTarget({
            id: adj?.adjustment_id,
            name: adj?.reason || `#${adj?.adjustment_id}`,
          });
          setIsDeleteAdjustmentOpen(true);
        }}
      />

      <DeleteConfirmModal
        isOpen={isDeleteAdjustmentOpen}
        onClose={() => {
          setIsDeleteAdjustmentOpen(false);
          setDeleteAdjustmentTarget(null);
        }}
        data={deleteAdjustmentTarget}
        title="điều chỉnh"
        onConfirm={(id) => handleConfirmDeleteAdjustment(id)}
      />
    </div>
  );
}
