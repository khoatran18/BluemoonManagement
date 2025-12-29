import React, { useState, useEffect } from "react";
import { getResidents, deleteResident, editResident, createResident } from "../../../../api/residentApi";
import Table from "../../../../Components/Table/Table"
import Column from "../../../../Components/Table/Column";
import { ActionMenu } from "../../../../Components/ActionMenu";
import { DetailResidentModal } from "../../../../Components/DetailResidentModal";
import { EditResidentModal } from "../../../../Components/EditResidentModal";
import { DeleteConfirmModal } from "../../../../Components/DeleteConfirmModal";
import "../../../Fee/Fee.css";

export const DataTableSection = ({ searchQuery = "" }) => {
  const [selectedResident, setSelectedResident] = useState(null);
  const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [page, setPage] = useState(1);
  const [limit, setLimit] = useState(5);

  const [data, setData] = useState([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  // Optionally, you can fetch apartments for dropdowns, etc.
  // const [apartments, setApartments] = useState([]);

  const fetchData = async () => {
    setLoading(true);
    try {
      const params = { page, limit, ...(searchQuery ? { full_name: searchQuery } : {}) };
      console.log("[ResidentDataTable] fetchResidents params:", params);
      const res = await getResidents(params);
      setData(
        (res.residents || []).map(item => ({
          id: item.resident_id,
          full_name: item.full_name,
          email: item.email,
          phone_number: item.phone_number,
          apartment: item.apartment || null,
          is_head: item.is_head,
        }))
      );
      setTotal(res.total_items || 0);
    } catch (err) {
      console.error("[ResidentDataTable] fetchResidents error:", err);
      setData([]);
      setTotal(0);
    }
    setLoading(false);
  };

  useEffect(() => {
    console.log("[ResidentDataTable] mounted/changed deps", { page, limit, searchQuery });
    fetchData();
  }, [page, limit, searchQuery]);

  const handleViewDetails = (recordId) => {
    const resident = data.find(r => r.id === recordId);
    setSelectedResident(resident);
    setIsDetailModalOpen(true);
  };

  const handleEdit = (recordId) => {
    const resident = data.find(r => r.id === recordId);
    setSelectedResident(resident);
    setIsEditModalOpen(true);
  };

  const handleDelete = (recordId) => {
    const resident = data.find(r => r.id === recordId);
    setSelectedResident(resident);
    setIsDeleteModalOpen(true);
  };

  const handleDetailClose = () => {
    setIsDetailModalOpen(false);
    setSelectedResident(null);
  };

  const handleEditClose = () => {
    setIsEditModalOpen(false);
    setSelectedResident(null);
  };

  const handleDeleteClose = () => {
    setIsDeleteModalOpen(false);
    setSelectedResident(null);
  };

  const handleDeleteConfirm = async (residentId) => {
    try {
      await deleteResident(residentId);
      setIsDeleteModalOpen(false);
      setSelectedResident(null);
      // Reset page và refresh danh sách sau khi xóa
      setPage(1);
      console.log(`Xóa cư dân ${residentId} thành công`);
    } catch (err) {
      console.error('Error deleting resident:', err);
    }
  };

  const handleEditSubmit = async (formData) => {
    try {
      await editResident(selectedResident.id, formData);
      setIsEditModalOpen(false);
      setSelectedResident(null);
      // Refresh danh sách sau khi cập nhật
      fetchData();
      console.log("Cập nhật cư dân thành công:", formData);
    } catch (err) {
      console.error('Error updating resident:', err);
    }
  };

  return (
    <>
      {loading ? (
        <div className="fee-spinner">
          <div></div>
        </div>
      ) : (
        <Table
          data={data}
          total={total}
          page={page}
          limit={limit}
          onPageChange={setPage}
          onLimitChange={(l) => {
            setLimit(l);
            setPage(1);
          }}
        >
          <Column dataIndex="id" title="Mã cư dân" sortable key="id" />
          <Column dataIndex="full_name" title="Tên cư dân" sortable key="full_name" />
          <Column dataIndex="email" title="Email" key="email" />
          <Column dataIndex="phone_number" title="Số điện thoại" key="phone_number" />
          <Column
            dataIndex="apartment"
            title="Căn hộ"
            key="apartment"
            render={(_, record) =>
              record.apartment
                ? `${record.apartment.building} - ${record.apartment.room_number}`
                : ""
            }
          />
          <Column
            dataIndex="is_head"
            title="Chủ hộ"
            key="is_head"
            render={(_, record) => (record.is_head ? "✔" : "")}
          />
          <Column
            dataIndex="actions"
            title="Thao tác"
            key="actions"
            render={(_, record) => (
              <ActionMenu
                recordId={record.id}
                actions={[
                  {
                    label: "Xem",
                    icon: "view",
                    type: "view",
                    onClick: () => handleViewDetails(record.id),
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

      <DetailResidentModal
        isOpen={isDetailModalOpen}
        onClose={handleDetailClose}
        resident={selectedResident}
      />

      <EditResidentModal
        isOpen={isEditModalOpen}
        onClose={handleEditClose}
        resident={selectedResident}
        onSubmit={handleEditSubmit}
      />

      <DeleteConfirmModal
        isOpen={isDeleteModalOpen}
        onClose={handleDeleteClose}
        data={selectedResident ? { id: selectedResident.id, name: selectedResident.full_name } : null}
        title="dân cư"
        onConfirm={handleDeleteConfirm}
      />
    </>
  );
};
