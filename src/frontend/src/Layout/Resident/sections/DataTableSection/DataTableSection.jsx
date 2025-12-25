import { number } from "prop-types";
import React, { useState } from "react";
import Table from "../../../../Components/Table/Table"
import Column from "../../../../Components/Table/Column";
import { ActionMenu } from "../../../../Components/ActionMenu";
import { DetailResidentModal } from "../../../../Components/DetailResidentModal";
import { EditResidentModal } from "../../../../Components/EditResidentModal";
import { DeleteConfirmModal } from "../../../../Components/DeleteConfirmModal";

export const DataTableSection = ({ searchQuery = "" }) => {
  const [selectedResident, setSelectedResident] = useState(null);
  const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [page, setPage] = useState(1);
  const [limit, setLimit] = useState(5);

  const apartments = [
    {
      id: "",
      building: "",
      room_number: "",
      apartment_owner: "",
      operation: "",
    },

  ];

  const allData = Array.from({ length: 32 }).map((_, i) => ({
    id: `#${i + 1000}`,
    name: `Cư dân ${i + 1}`,
    room: `A${600 + i}`,
  }));

  // Lọc dữ liệu theo search query
  const data = searchQuery.trim() === "" 
    ? allData 
    : allData.filter(resident => {
        const searchLower = searchQuery.toLowerCase();
        return (
          resident.id.toLowerCase().includes(searchLower) ||
          resident.name.toLowerCase().includes(searchLower) ||
          resident.room.toLowerCase().includes(searchLower)
        );
      });

  console.log("Search Query:", searchQuery);
  console.log("Filtered Data Count:", data.length);

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

  const handleDeleteConfirm = (residentId) => {
    console.log(`Xóa cư dân: ${residentId}`);
    setIsDeleteModalOpen(false);
    setSelectedResident(null);
    // Thêm logic xóa ở đây
  };

  const handleEditSubmit = (formData) => {
    console.log("Cập nhật cư dân:", formData);
    setIsEditModalOpen(false);
    setSelectedResident(null);
  };

  return (
    <>
      <Table data={data}
        page={page}
        limit={limit}
        onPageChange={setPage}
        onLimitChange={(l) => {
          setLimit(l);
          setPage(1);
        }}>
        <Column dataIndex="id" title="Mã căn hộ" sortable />
        <Column dataIndex="name" title="Tên cư dân" sortable />
        <Column dataIndex="room" title="Số phòng" sortable />
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
        resident={selectedResident}
        onConfirm={handleDeleteConfirm}
      />
    </>
  );
};
