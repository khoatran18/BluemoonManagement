import { useState, useEffect } from "react";
import { ActionMenu } from "../../../Components/ActionMenu";
import Table from "../../../Components/Table/Table";
import Column from "../../../Components/Table/Column";
import Tag from "../../../Components/Tag/Tag";
import { getFees, getFeeDetails, deleteFee } from "../../../api/feeApi";
import { DetailFeeModal } from "../../../Components/DetailFeeModal/DetailFeeModal";
import { DeleteConfirmModal } from "../../../Components/DeleteConfirmModal";
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
    obligatory: 1,
    voluntary: 2,
    impromptu: 3
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

  const [page, setPage] = useState(1);
  const [limit, setLimit] = useState(10);

  const fetchFees = async () => {
    setLoading(true);
    setError(null);
    try {
      const params = { page, limit };

      if (activeType && activeType.length > 0) {
        const typeIds = activeType.map(t => typeIdMap[t.toLowerCase()]).filter(Boolean);
        if (typeIds.length === 1) params.fee_type_id = typeIds[0];
        else if (typeIds.length > 1) params.fee_type_id = typeIds.join(',');
      }

      if (activeStatus && activeStatus.length > 0) {
        params.status = activeStatus.join(',');
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
      setError(err.message || String(err));
      setData([]);
    } finally {
      setLoading(false);
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

  return (
    <div className="fee-data-table">
      {loading ? (
        <div className="fee-spinner">
          <div></div>
        </div>
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
          <Column dataIndex="id" title="Mã phí" sortable />
          <Column dataIndex="name" title="Tên khoản phí" sortable className="fee-name-column" />

          <Column
            dataIndex="type"
            title="Loại phí"
            render={(type) => (
              <Tag variant="Fee" type={type}>
                {typeMap[type] || "Khác"}
              </Tag>
            )}
          />

          <Column dataIndex="value" title="Số tiền" sortable />

          <Column
            dataIndex="status"
            title="Trạng thái"
            render={(status) => (
              <Tag variant="Status" status={status.toLowerCase()}>
                {statusMap[status.toLowerCase()] || "Khác"}
              </Tag>
            )}
          />

          <Column
            dataIndex="actions"
            title="Thao tác"
            render={(_, record) => (
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
            )}
          />
        </Table>
      )}

      {error && <p>Error loading fees: {error}</p>}


      {/* ActionMenu handles its own dropdown rendering; legacy menu code removed */}
      <DetailFeeModal isOpen={isDetailOpen} onClose={() => setIsDetailOpen(false)} fee={selectedFee} loading={detailLoading} />
      <DeleteConfirmModal
        isOpen={isDeleteOpen}
        onClose={() => { setIsDeleteOpen(false); setDeleteTarget(null); }}
        data={deleteTarget}
        title="phí"
        onConfirm={(id) => handleConfirmDelete(id)}
      />
    </div>
  );
}
