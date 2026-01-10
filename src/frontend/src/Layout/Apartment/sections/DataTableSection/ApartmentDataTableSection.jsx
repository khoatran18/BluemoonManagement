import React, { useEffect, useState } from "react";
import Table from "../../../../Components/Table/Table"
import Column from "../../../../Components/Table/Column";
import { ActionMenu } from "../../../../Components/ActionMenu";
import { EditApartmentModal } from "../../../../Components/EditApartmentModal";
import { DetailApartmentModal } from "../../../../Components/DetailApartmentModal";
import { DeleteConfirmModal } from "../../../../Components/DeleteConfirmModal";
import { deleteApartment, editApartment, getApartments, getApartmentDetail } from "../../../../api/apartmentApi";
import LoadingSpinner from "../../../../Components/LoadingSpinner/LoadingSpinner";
import { getResidentDetail } from "../../../../api/residentApi";
import { usePersistentState } from "../../../../hooks/usePersistentState";

export const ApartmentDataTableSection = ({ searchQuery = "", onNotify, refreshKey = 0 }) => {
  const [selectedApartment, setSelectedApartment] = useState(null);
  const [detailApartment, setDetailApartment] = useState(null);
  const [detailLoading, setDetailLoading] = useState(false);
  const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [page, setPage] = usePersistentState('apartment.table.page', 1);
  const [limit, setLimit] = usePersistentState('apartment.table.limit', 5);
  const [reloadKey, setReloadKey] = useState(0);

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
  }, [page, limit, searchQuery, reloadKey, refreshKey]);

  const handleEdit = (recordId) => {
    const apartment = data.find(a => a.id === recordId);
    setSelectedApartment(apartment);
    setIsEditModalOpen(true);
  };

  const handleViewDetails = (apartmentId) => {
    setIsDetailModalOpen(true);
    setDetailApartment(null);
    setDetailLoading(true);

    (async () => {
      try {
        const resp = await getApartmentDetail(apartmentId);

        let payload = resp;
        if (payload && payload.data) payload = payload.data;
        if (payload && payload.data) payload = payload.data;

        const headResidentId = payload?.head_resident_id ?? null;
        let headResidentData = null;
        if (headResidentId !== null) {
          const headResp = await getResidentDetail(headResidentId)
          headResidentData = headResp;
          if (headResidentData && headResidentData.data) headResidentData = headResidentData.data;
          if (headResidentData && headResidentData.data) headResidentData = headResidentData.data;
        }

        setDetailApartment({
          id: payload?.apartment_id ?? payload?.id ?? apartmentId,
          building: payload?.building,
          room_number: payload?.room_number,
          head_resident: headResidentData ?? null,
          head_resident_id: payload?.head_resident_id ?? null,
          residents: Array.isArray(payload?.residents) ? payload.residents : [],
          resident_count: Array.isArray(payload?.residents) ? payload.residents.length : undefined,
        });
      } catch (err) {
        setDetailApartment(null);
        if (typeof onNotify === 'function') {
          onNotify({ message: err?.message || 'Tải chi tiết căn hộ thất bại', variant: 'error', duration: 4000 });
        }
      } finally {
        setDetailLoading(false);
      }
    })();
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

  const handleDetailClose = () => {
    setIsDetailModalOpen(false);
    setDetailApartment(null);
    setDetailLoading(false);
  };

  const handleDeleteClose = () => {
    setIsDeleteModalOpen(false);
    setSelectedApartment(null);
  };

  const handleDeleteConfirm = async (apartmentId) => {
    try {
      await deleteApartment(apartmentId);

      if (typeof onNotify === 'function') {
        onNotify({ message: 'Xóa căn hộ thành công!', variant: 'success', duration: 3000 });
      }

      // If this was the last row on the page, try moving back a page.
      if (data.length <= 1 && page > 1) {
        setPage(page - 1);
      } else {
        setReloadKey((k) => k + 1);
      }
    } catch (err) {
      if (typeof onNotify === 'function') {
        onNotify({ message: err?.message || 'Xóa căn hộ thất bại', variant: 'error', duration: 4000 });
      }
    } finally {
      setIsDeleteModalOpen(false);
      setSelectedApartment(null);
    }
  };

  const handleEditSubmit = (formData) => {
    (async () => {
      try {
        const apartmentId = formData?.id;
        if (!apartmentId) throw new Error('Thiếu mã căn hộ');

        await editApartment(apartmentId, {
          building: formData?.building,
          room_number: formData?.room_number,
          head_resident_id: formData?.head_resident_id ?? null,
          residents: Array.isArray(formData?.residents) ? formData.residents : [],
        });

        if (typeof onNotify === 'function') {
          onNotify({ message: 'Cập nhật căn hộ thành công!', variant: 'success', duration: 3000 });
        }

        setReloadKey((k) => k + 1);
        setIsEditModalOpen(false);
        setSelectedApartment(null);
      } catch (err) {
        if (typeof onNotify === 'function') {
          onNotify({ message: err?.message || 'Cập nhật căn hộ thất bại', variant: 'error', duration: 4000 });
        }
      }
    })();
  };

  return (
    <>
      {loading ? (
        <LoadingSpinner />
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

      <DetailApartmentModal
        isOpen={isDetailModalOpen}
        onClose={handleDetailClose}
        apartment={detailApartment}
        loading={detailLoading}
      />

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
