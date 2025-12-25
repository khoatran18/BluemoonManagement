import React, { useState } from "react";
import Table from "../../../../Components/Table/Table"
import Column from "../../../../Components/Table/Column";
import { ActionMenu } from "../../../../Components/ActionMenu";
import { EditApartmentModal } from "../../../../Components/EditApartmentModal";
import { DeleteConfirmModal } from "../../../../Components/DeleteConfirmModal";

export const ApartmentDataTableSection = ({ searchQuery = "" }) => {
  const [selectedApartment, setSelectedApartment] = useState(null);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [page, setPage] = useState(1);
  const [limit, setLimit] = useState(5);

  // Mock data for apartments
  const allData = Array.from({ length: 32 }).map((_, i) => ({
    id: `A${600 + i}`,
    building: i % 3 === 0 ? "A" : i % 3 === 1 ? "B" : "C",
    room: `${600 + i}`,
    area: `${50 + (i % 30)}m²`,
    status: i % 2 === 0 ? "Trống" : "Có dân cư",
  }));

  // Filter data based on search query
  const data = searchQuery.trim() === "" 
    ? allData 
    : allData.filter(apartment => {
        const searchLower = searchQuery.toLowerCase();
        return (
          apartment.id.toLowerCase().includes(searchLower) ||
          apartment.building.toLowerCase().includes(searchLower) ||
          apartment.room.toLowerCase().includes(searchLower) ||
          apartment.area.toLowerCase().includes(searchLower)
        );
      });

  console.log("Search Query:", searchQuery);
  console.log("Filtered Data Count:", data.length);

  const handleEdit = (recordId) => {
    const apartment = data.find(a => a.id === recordId);
    setSelectedApartment(apartment);
    setIsEditModalOpen(true);
  };

  const handleDelete = (recordId) => {
    const apartment = data.find(a => a.id === recordId);
    setSelectedApartment(apartment);
    setIsDeleteModalOpen(true);
  };

  const handleEditClose = () => {
    setIsEditModalOpen(false);
    setSelectedApartment(null);
  };

  const handleDeleteClose = () => {
    setIsDeleteModalOpen(false);
    setSelectedApartment(null);
  };

  const handleDeleteConfirm = (apartmentId) => {
    console.log(`Xóa căn hộ: ${apartmentId}`);
    setIsDeleteModalOpen(false);
    setSelectedApartment(null);
  };

  const handleEditSubmit = (formData) => {
    console.log("Cập nhật căn hộ:", formData);
    setIsEditModalOpen(false);
    setSelectedApartment(null);
  };

  return (
    <>
      <Table        
        data={data}
        page={page}
        limit={limit}
        onPageChange={setPage}
        onLimitChange={(l) => {
          setLimit(l);
          setPage(1);
        }}>
        <Column dataIndex="id" title="Mã căn hộ" sortable />
        <Column dataIndex="building" title="Tòa nhà" sortable />
        <Column dataIndex="room" title="Số phòng" sortable />
        <Column dataIndex="area" title="Diện tích" sortable />
        <Column dataIndex="status" title="Trạng thái" sortable />
        <Column
          dataIndex="actions"
          title="Thao tác"
          render={(_, record) => (
            <ActionMenu
              recordId={record.id}
              actions={[
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

      <EditApartmentModal
        isOpen={isEditModalOpen}
        onClose={handleEditClose}
        apartment={selectedApartment}
        onSubmit={handleEditSubmit}
      />

      <DeleteConfirmModal
        isOpen={isDeleteModalOpen}
        onClose={handleDeleteClose}
        resident={selectedApartment}
        onConfirm={handleDeleteConfirm}
      />
    </>
  );
};
