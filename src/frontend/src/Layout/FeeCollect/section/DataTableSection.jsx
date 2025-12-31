import React, { useEffect, useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import Table from "../../../Components/Table/Table";
import Column from "../../../Components/Table/Column";
import Button from "../../../Components/Button/Button";
import { getApartments, getApartmentDetail } from "../../../api/apartmentApi";

import "../../Fee/Fee.css";
import "../FeeCollect.css";

const FEE_COLLECTION_TABLE_STATE_KEY = "fee-collection-table-state:v1";

function readFeeCollectionTableState() {
	try {
		const raw = sessionStorage.getItem(FEE_COLLECTION_TABLE_STATE_KEY);
		if (!raw) return null;
		const parsed = JSON.parse(raw);
		return parsed && typeof parsed === "object" ? parsed : null;
	} catch {
		return null;
	}
}

function writeFeeCollectionTableState(state) {
	try {
		sessionStorage.setItem(FEE_COLLECTION_TABLE_STATE_KEY, JSON.stringify(state));
	} catch {
		
	}
}

export const FeeCollectDataTableSection = ({ searchQuery = "" }) => {
	const saved = readFeeCollectionTableState();
	const canRestore =
		saved &&
		typeof saved === "object" &&
		saved.searchQuery === searchQuery &&
		Array.isArray(saved.data) &&
		typeof saved.page === "number" &&
		typeof saved.limit === "number";

	const navigate = useNavigate();
	const [page, setPage] = useState(canRestore ? saved.page : 1);
	const [limit, setLimit] = useState(canRestore ? saved.limit : 5);

	const [data, setData] = useState(canRestore ? saved.data : []);
	const [total, setTotal] = useState(canRestore && typeof saved.total === "number" ? saved.total : 0);
	const [loading, setLoading] = useState(false);
	const skipInitialFetchRef = useRef(canRestore && saved.data.length > 0);

	useEffect(() => {
		if (skipInitialFetchRef.current) {
            console.log("Skipping initial fetch of FeeCollectDataTableSection due to restored state.");
			skipInitialFetchRef.current = false;
			return;
		}

		const fetchData = async () => {
            console.log("Fetching apartment data for FeeCollectDataTableSection with searchQuery:", searchQuery);
			setLoading(true);
			try {
				const params = {
					page,
					limit,
					...(searchQuery ? { room_number: searchQuery } : {}),
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

	useEffect(() => {
		writeFeeCollectionTableState({
			searchQuery,
			page,
			limit,
			data,
			total,
			savedAt: Date.now(),
		});
	}, [searchQuery, page, limit, data, total]);

	const handleViewDetail = (recordId) => {
		writeFeeCollectionTableState({
			searchQuery,
			page,
			limit,
			data,
			total,
			savedAt: Date.now(),
		});
		navigate(`/fee-collection/apartment/${recordId}/status`);
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
							<div className="fee-collect-actions">
								<Button
									className="fee-collect-btn-secondary"
									icon={null}
									onClick={() => handleViewDetail(record.id)}
								>
									Chi tiết
								</Button>
							</div>
						)}
					/>
				</Table>
			)}
		</>
	);
};

