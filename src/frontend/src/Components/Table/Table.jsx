import React, { useState } from "react";
import "./Table.css";
import Column from "./Column";
import Pagination from "../Pagination/Pagination";

export default function Table({ children, data, pageSize = 10, onRowSelect }) {
    const columns = React.Children.toArray(children)
        .filter((child) => child.type === Column)
        .map((child) => child.props);

    // sort
    const [sortConfig, setSortConfig] = useState({ key: null, direction: null });

    const handleSort = (key) => {
        const direction =
        sortConfig.key === key && sortConfig.direction === "asc" ? "desc" : "asc";
        setSortConfig({ key, direction });
    };

    const sortedData = React.useMemo(() => {
        if (!sortConfig.key) return data;

        return [...data].sort((a, b) => {
        const v1 = a[sortConfig.key];
        const v2 = b[sortConfig.key];

        if (v1 < v2) return sortConfig.direction === "asc" ? -1 : 1;
        if (v1 > v2) return sortConfig.direction === "asc" ? 1 : -1;
        return 0;
        });
    }, [data, sortConfig]);


    // selected row
    const [selectedIndex, setSelectedIndex] = useState(null);
    const [page, setPage] = useState(1);

    const start = (page - 1) * pageSize;
    const currentData = sortedData.slice(start, start + pageSize);

    const handleRowClick = (index, row) => {
        setSelectedIndex(index);
        onRowSelect?.(row);
    };

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
                        {col.sortable && (
                        <span className="sort-icon">
                            {sortConfig.key !== col.key
                            ? "↕"
                            : sortConfig.direction === "asc"
                            ? "↑"
                            : "↓"}
                        </span>
                        )}
                    </th>
                    ))}
                </tr>
                </thead>

                <tbody>
                    {currentData.map((row, index) => (
                    <tr
                        key={start + index}
                        className={selectedIndex === index ? "selected-row" : ""}
                        onClick={(e) => handleRowClick(index, row, e)}
                    >
                        {columns.map((col) => (
                        <td key={col.dataIndex}>
                            {col.render
                            ? col.render(row[col.dataIndex], row)
                            : row[col.dataIndex]}
                        </td>
                        ))}
                    </tr>
                    ))}
                </tbody>
            </table>
            </div>

            {/* Pagination */}
            <Pagination
                total={sortedData.length}
                current={page}
                pageSize={pageSize}
                onChange={(p) => setPage(p)}
            />
        </div>
    );
}
