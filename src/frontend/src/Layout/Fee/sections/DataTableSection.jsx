import { useState, useEffect, useMemo } from "react";
import { ActionMenu } from "../../../Components/ActionMenu";
import Table from "../../../Components/Table/Table";
import Column from "../../../Components/Table/Column";
import Tag from "../../../Components/Tag/Tag";
import { getFees, getFeeDetails, deleteFee } from "../../../api/feeApi";
import { DetailFeeModal } from "../../../Components/DetailFeeModal/DetailFeeModal";
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


export default function DataTableSection({ activeType, activeStatus, search, onEditRequest, registerRefresh, onNotify }) {
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

  const [page, setPage] = useState(1);
  const [limit, setLimit] = useState(5);

  const fetchFees = async () => {
    setLoading(true);
    setError(null);
    try {
      const params = {
        page,
        limit
      };
      const response = await getFees(params);
      setData(response.fees.map(fee => ({
        id: fee.fee_id,
        name: fee.fee_name,
        type: Object.keys(typeIdMap).find(key => typeIdMap[key] === fee.fee_type_id) || 'unknown',
        value: fee.fee_amount.toLocaleString("vi-VN") + "đ",
        status: fee.status
      })));
      setTotalItems(response.total_items);
    } catch (err) {
      setError(err.message);
      setData([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchFees();

    if (typeof registerRefresh === "function") {
      registerRefresh(fetchFees);
    }
  }, [page, limit]);

  const filteredData = useMemo(() => {
    let result = [...data];

    if (activeType.length > 0) {
      result = result.filter(item => activeType.includes(item.type));
    }

    if (activeStatus.length > 0) {
      result = result.filter(item => activeStatus.some(s => s.toLowerCase() === item.status.toLowerCase()));
    }

    if (search.trim() !== "") {
      result = result.filter(item =>
        item.name.toLowerCase().includes(search.toLowerCase())
      );
    }

    return result;
  }, [data, activeType, activeStatus, search]);

  useEffect(() => {
    setPage(1);
  }, [activeType, activeStatus, search]);

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
    try {
      if (!row) return;
      await deleteFee(row);
      // refresh list after delete
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
          data={filteredData}
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
                    onClick: () => handleDelete(record.id),
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
    </div>
  );
}
