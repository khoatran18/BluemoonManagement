import React, { useMemo } from "react";
import "./Table.css";
import Column from "./Column";
import Pagination from "../Pagination/Pagination";

export default function Table({
  children,
  data,
  page,
  limit,
  onPageChange,
  onLimitChange,
  onRowSelect
}) {
  const columns = React.Children.toArray(children)
    .filter((child) => child.type === Column)
    .map((child) => child.props);

  // SORT
  const [sortConfig, setSortConfig] = React.useState({
    key: null,
    direction: null,
  });

  const handleSort = (key) => {
    const direction =
      sortConfig.key === key && sortConfig.direction === "asc"
        ? "desc"
        : "asc";

    setSortConfig({ key, direction });
  };

  const sortedData = useMemo(() => {
    if (!sortConfig.key) return data;

    return [...data].sort((a, b) => {
      const v1 = a[sortConfig.key];
      const v2 = b[sortConfig.key];

      if (v1 < v2) return sortConfig.direction === "asc" ? -1 : 1;
      if (v1 > v2) return sortConfig.direction === "asc" ? 1 : -1;
      return 0;
    });
  }, [data, sortConfig]);

  const start = (page - 1) * limit;
  const currentData = sortedData.slice(start, start + limit);

  return (
    <div>
      <div className="table-wrapper">
        <table className="base-table">
          <thead>
            <tr>
              {columns.map((col) => (
                <th
                  key={col.dataIndex}
                  onClick={col.sortable ? () => handleSort(col.key) : undefined}
                  className={col.sortable ? "sortable" : ""}
                >
                  {col.title}
                </th>
              ))}
            </tr>
          </thead>

          <tbody>
            {currentData.map((row, index) => (
              <tr
                key={start + index}
                onClick={() => onRowSelect?.(row)}
              >
                {columns.map((col) => (
                  <td key={col.dataIndex}>
                    <div>
                      {col.render
                        ? col.render(row[col.dataIndex], row)
                        : row[col.dataIndex]}
                    </div>
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <Pagination
        total={sortedData.length}
        page={page}
        limit={limit}
        onPageChange={onPageChange}
        onLimitChange={onLimitChange}
      />
    </div>
  );
}
