import React, { useEffect, useState } from "react";
import Table from "../../../../Components/Table/Table"
import Column from "../../../../Components/Table/Column";
import { ActionMenu } from "../../../../Components/ActionMenu";
import { EditApartmentModal } from "../../../../Components/EditApartmentModal";
import { DeleteConfirmModal } from "../../../../Components/DeleteConfirmModal";
import { getApartments, getApartmentDetail } from "../../../../api/apartmentApi";
import "../../../Fee/Fee.css";

export const ApartmentDataTableSection = ({ searchQuery = "" }) => {
  const [selectedApartment, setSelectedApartment] = useState(null);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [page, setPage] = useState(1);
  const [limit, setLimit] = useState(5);

  const [data, setData] = useState([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      try {
        const params = {
          page,
          limit,
          ...(searchQuery ? { room_number: searchQuery } : {})
        };

        const res = await getApartments(params);
        const items = res.items || [];

        const rows = items.map((item) => ({
          id: item.apartment_id,
          building: item.building,
          room: item.room_number,
          head_resident: item.head_resident,
          resident_count: undefined,
        }));

        setData(rows);
        setTotal(res.total_items || 0);

        // Use the apartment detail API to fill the missing column (resident count)
        const detailResults = await Promise.allSettled(
          rows.map((row) => getApartmentDetail(row.id))
        );

        setData((prev) =>
          prev.map((row, idx) => {
            const detail = detailResults[idx];
            if (detail && detail.status === "fulfilled") {
              const residents = detail.value?.residents || [];
              return { ...row, resident_count: residents.length };
            }
            return row;
          })
        );
      } catch (err) {
        setData([]);
        setTotal(0);
      }
      setLoading(false);
    };

    fetchData();
  }, [page, limit, searchQuery]);

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
          <Column dataIndex="id" title="Mã căn hộ" sortable key="id" />
          <Column dataIndex="building" title="Tòa nhà" sortable key="building" />
          <Column dataIndex="room" title="Số phòng" sortable key="room" />
          <Column
            dataIndex="head_resident"
            title="Trưởng cư dân"
            key="head_resident"
            render={(_, record) => record.head_resident?.full_name || ""}
          />
          <Column
            dataIndex="resident_count"
            title="Số cư dân"
            sortable
            key="resident_count"
            render={(value) => (typeof value === "number" ? value : "")}
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

      <EditApartmentModal
        isOpen={isEditModalOpen}
        onClose={handleEditClose}
        apartment={selectedApartment}
        onSubmit={handleEditSubmit}
      />

      <DeleteConfirmModal
        isOpen={isDeleteModalOpen}
        onClose={handleDeleteClose}
        data={selectedApartment}
        title="căn hộ"
        onConfirm={handleDeleteConfirm}
      />
    </>
  );
};
