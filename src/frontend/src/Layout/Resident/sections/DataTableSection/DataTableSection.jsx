import React, { useState, useEffect } from "react";
import { getResidents, deleteResident, editResident, getResidentDetail } from "../../../../api/residentApi";
import Table from "../../../../Components/Table/Table"
import Column from "../../../../Components/Table/Column";
import { ActionMenu } from "../../../../Components/ActionMenu";
import { DetailResidentModal } from "../../../../Components/DetailResidentModal";
import { EditResidentModal } from "../../../../Components/EditResidentModal";
import { DeleteConfirmModal } from "../../../../Components/DeleteConfirmModal";
import "../../../Fee/Fee.css";
import { usePersistentState } from "../../../../hooks/usePersistentState";

export const DataTableSection = ({
  searchQuery = "",
  apartmentId = "",
  phoneNumber = "",
  email = "",
  refreshKey = 0,
  onNotify,
}) => {
  const [selectedResident, setSelectedResident] = useState(null);
  const [detailLoading, setDetailLoading] = useState(false);
  const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [page, setPage] = usePersistentState('resident.table.page', 1);
  const [limit, setLimit] = usePersistentState('resident.table.limit', 5);

  const [data, setData] = useState([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);

  const fetchData = async () => {
    setLoading(true);
    try {
      const params = {
        page,
        limit,
        ...(searchQuery ? { full_name: searchQuery } : {}),
        ...(apartmentId ? { apartment_id: apartmentId } : {}),
        ...(phoneNumber ? { phone_number: phoneNumber } : {}),
        ...(email ? { email } : {}),
      };
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
  }, [page, limit, searchQuery, apartmentId, phoneNumber, email, refreshKey]);

  useEffect(() => {
    // When filters/search change, go back to page 1.
    setPage(1);
  }, [searchQuery, apartmentId, phoneNumber, email, limit, refreshKey]);

  const handleViewDetails = (residentId) => {
    setIsDetailModalOpen(true);
    setSelectedResident(null);
    setDetailLoading(true);

    (async () => {
      try {
        const resp = await getResidentDetail(residentId);

        let payload = resp;
        if (payload && payload.data) payload = payload.data;
        if (payload && payload.data) payload = payload.data;

        const apartment = payload?.apartment || null;
        const normalized = {
          id: payload?.resident_id ?? payload?.id ?? residentId,
          full_name: payload?.full_name ?? payload?.name ?? "",
          name: payload?.full_name ?? payload?.name ?? "",
          email: payload?.email ?? "",
          phone_number: payload?.phone_number ?? payload?.phone ?? "",
          phone: payload?.phone_number ?? payload?.phone ?? "",
          apartment,
          room: apartment?.room_number ?? payload?.room ?? "",
          building: apartment?.building ?? payload?.building ?? "",
          is_head: !!(payload?.is_head ?? payload?.isHead),
        };

        setSelectedResident(normalized);
      } catch (err) {
        console.error("Error fetching resident detail", err);
        setSelectedResident(null);
      } finally {
        setDetailLoading(false);
      }
    })();
  };


  const handleEdit = (recordId) => {
    const resident = data.find(r => r.id === recordId);
    const apartment = resident?.apartment || null;

    // Open modal immediately with best-effort data, then enrich via detail API.
    setIsEditModalOpen(true);
    setSelectedResident({
      id: resident?.id ?? recordId,
      full_name: resident?.full_name ?? "",
      name: resident?.full_name ?? "",
      email: resident?.email ?? "",
      phone_number: resident?.phone_number ?? "",
      phone: resident?.phone_number ?? "",
      apartment,
      room: apartment?.room_number ?? "",
      building: apartment?.building ?? "",
      is_head: !!resident?.is_head,
    });

    (async () => {
      try {
        const resp = await getResidentDetail(recordId);

        let payload = resp;
        if (payload && payload.data) payload = payload.data;
        if (payload && payload.data) payload = payload.data;

        const apartmentFromApi = payload?.apartment || apartment;
        const normalized = {
          id: payload?.resident_id ?? payload?.id ?? recordId,
          full_name: payload?.full_name ?? payload?.name ?? resident?.full_name ?? "",
          name: payload?.full_name ?? payload?.name ?? resident?.full_name ?? "",
          email: payload?.email ?? resident?.email ?? "",
          phone_number: payload?.phone_number ?? payload?.phone ?? resident?.phone_number ?? "",
          phone: payload?.phone_number ?? payload?.phone ?? resident?.phone_number ?? "",
          apartment: apartmentFromApi,
          room: apartmentFromApi?.room_number ?? payload?.room ?? "",
          building: apartmentFromApi?.building ?? payload?.building ?? "",
          is_head: !!(payload?.is_head ?? payload?.isHead ?? resident?.is_head),
        };

        setSelectedResident(normalized);
      } catch (err) {
        console.error("Error fetching resident detail for edit", err);
      }
    })();
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
      fetchData()
      if (typeof onNotify === 'function') onNotify({ message: 'Xóa cư dân thành công', variant: 'success', duration: 3000 });
    } catch (err) {
      console.error('Error deleting resident:', err);
      if (typeof onNotify === 'function') onNotify({ message: 'Xóa cư dân thất bại', variant: 'error', duration: 4000 });
    }
  };

  const handleEditSubmit = async (formData) => {
    try {
      await editResident(selectedResident.id, formData);
      setIsEditModalOpen(false);
      setSelectedResident(null);
      if (typeof onNotify === 'function') onNotify({ message: 'Cập nhật cư dân thành công', variant: 'success', duration: 3000 });
      fetchData();
    } catch (err) {
      console.error('Error updating resident:', err);
      if (typeof onNotify === 'function') onNotify({ message: 'Cập nhật cư dân thất bại', variant: 'error', duration: 4000 });
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
        loading={detailLoading}
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
