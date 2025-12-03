import { useState, useEffect, useMemo } from "react";
import editIcon from "../../../assets/icon/fee/pencil-filled.svg";
import deleteIcon from "../../../assets/icon/fee/trash-filled.svg";
import Table from "../../../Components/Table/Table";
import Column from "../../../Components/Table/Column";
import Tag from "../../../Components/Tag/Tag";

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

function FeeMenu({ onEdit, onDelete }) {
  return (
    <div className="fee-menu">
      <button className="fee-item edit" onClick={onEdit}>
        <span>Sửa</span>
        <img src={editIcon} alt="Edit Icon" />
      </button>

      <button className="fee-item delete" onClick={onDelete}>
        <span>Xóa</span>
        <img src={deleteIcon} alt="Delete Icon" />
      </button>
    </div>
  );
}

export default function DataTableSection({ activeType, activeStatus, search }) {
  const feeTypes = [
    { key: "obligatory", label: "Định kỳ" },
    { key: "voluntary", label: "Tự nguyện" },
    { key: "impromptu", label: "Đột xuất" }
  ];

  const statuses = ["active", "draft", "closed", "archived"];

  const data = Array.from({ length: 50 }).map((_, i) => {
    const feeType = feeTypes[i % feeTypes.length];
    const status = statuses[i % statuses.length];
    const value = 100000 + i * 50000;

    return {
      id: `FEE-${1000 + i}`,
      name: `Khoản phí ${i + 1}`,
      type: feeType.key,
      value: value.toLocaleString("vi-VN") + "đ",
      status
    };
  });

  const [page, setPage] = useState(1);
  const [limit, setLimit] = useState(5);

  const filteredData = useMemo(() => {
     let result = [...data];

    if (activeType.length > 0) {
      result = result.filter(item => activeType.includes(item.type));
    }

    if (activeStatus.length > 0) {
      result = result.filter(item => activeStatus.includes(item.status));
    }

    if (search.trim() !== "") {
      result = result.filter(item =>
        item.name.toLowerCase().includes(search.toLowerCase())
      );
    }

    return result;
  }, [activeType, activeStatus, search]);

  useEffect(() => {
    setPage(1);
  }, [activeType, activeStatus, search]);


  // Menu state
  const [menuPosition, setMenuPosition] = useState(null);
  const [menuRow, setMenuRow] = useState(null);

  const handleOpenMenu = (row, event) => {
    event.stopPropagation();
    setMenuRow(row);
    setMenuPosition({
      x: event.clientX,
      y: event.clientY
    });
  };

  const handleEdit = (row) => {
    console.log("Edit:", row);
  };

  const handleDelete = (row) => {
    console.log("Delete:", row);
  };

  useEffect(() => {
    const close = () => setMenuPosition(null);
    window.addEventListener("click", close);
    return () => window.removeEventListener("click", close);
  }, []);

  return (
    <div className="fee-data-table">
      <Table
        data={filteredData}
        page={page}
        limit={limit}
        onPageChange={setPage}
        onLimitChange={(l) => {
          setLimit(l);
          setPage(1);
        }}
      >
        <Column dataIndex="id" title="Mã phí" sortable />
        <Column dataIndex="name" title="Tên khoản phí" sortable />

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
            <Tag variant="Status" status={status}>
              {statusMap[status] || "Khác"}
            </Tag>
          )}
        />

        <Column
          dataIndex="actions"
          title="Thao tác"
          render={(_, row) => (
            <button
              className="action-btn"
              onClick={(e) => handleOpenMenu(row, e)}
            >
              ⋮
            </button>
          )}
        />
      </Table>


      {menuPosition && (
        <div
          style={{
            position: "fixed",
            top: menuPosition.y,
            left: menuPosition.x,
            zIndex: 9999
          }}
        >
          <FeeMenu
            onEdit={() => handleEdit(menuRow)}
            onDelete={() => handleDelete(menuRow)}
          />
        </div>
      )}
    </div>
  );
}
