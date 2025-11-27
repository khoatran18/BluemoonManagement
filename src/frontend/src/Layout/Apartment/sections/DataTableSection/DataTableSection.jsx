import { number } from "prop-types";
import React from "react";
import Table from "../../../../Components/Table/Table"
import Column from "../../../../Components/Table/Column";

export const DataTableSection = () => {
  const apartments = [
    {
      id: "",
      building: "",
      room_number: "",
      apartment_owner: "",
      operation: "",
    },

  ];

  const data = Array.from({ length: 32 }).map((_, i) => ({
    id: `#${i + 1000}`,
    name: `Cư dân ${i + 1}`,
    room: `A${600 + i}`,
  }));


  return (
    <Table data={data} pageSize={5}>
      <Column dataIndex="id" title="Mã căn hộ" sortable />
      <Column dataIndex="name" title="Tên cư dân" sortable />
      <Column dataIndex="room" title="Số phòng" sortable />
      <Column
        dataIndex="actions"
        title="Thao tác"
        render={() => (
          <div
            style={{
              display: "flex",
              justifyContent: "center",
              fontSize: 20,
              cursor: "pointer"
            }}
          >
            ⋮
          </div>
        )}
      />
    </Table>
  );
};
