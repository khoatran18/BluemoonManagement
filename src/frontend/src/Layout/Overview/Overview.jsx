import { useEffect, useMemo, useState } from "react";
import "./Overview.css";
import { getApartmentCommonReport, getFeeCommonReport } from "../../api/reportApi";
import LoadingSpinner from "../../Components/LoadingSpinner/LoadingSpinner";

const formatNumber = (n) => {
    if (n == null || Number.isNaN(Number(n))) return "-";
    return new Intl.NumberFormat("vi-VN").format(Number(n));
};

const formatVND = (n) => {
    if (n == null || Number.isNaN(Number(n))) return "-";
    return new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(Number(n));
};

export default function Overview() {
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [apartmentReport, setApartmentReport] = useState(null);
    const [feeReport, setFeeReport] = useState(null);

    useEffect(() => {
        let cancelled = false;
        (async () => {
            try {
                setLoading(true);
                setError(null);

                const [apt, fee] = await Promise.all([
                    getApartmentCommonReport(),
                    getFeeCommonReport(),
                ]);

                if (cancelled) return;
                setApartmentReport(apt);
                setFeeReport(fee);
            } catch (err) {
                if (cancelled) return;
                setError(err?.message || "Không thể tải báo cáo");
            } finally {
                if (!cancelled) setLoading(false);
            }
        })();
        return () => {
            cancelled = true;
        };
    }, []);

    const feeStatusSeries = useMemo(() => {
        const active = Number(feeReport?.active_fee_count || 0);
        const draft = Number(feeReport?.draft_fee_count || 0);
        const closed = Number(feeReport?.closed_fee_count || 0);
        const archived = Number(feeReport?.archived_fee_count || 0);

        const items = [
            { key: "ACTIVE", label: "Đang hoạt động", value: active },
            { key: "DRAFT", label: "Nháp", value: draft },
            { key: "CLOSED", label: "Đã đóng", value: closed },
            { key: "ARCHIVED", label: "Lưu trữ", value: archived },
        ];
        const max = Math.max(1, ...items.map((i) => i.value));
        const total = items.reduce((s, i) => s + (Number(i.value) || 0), 0);

        const colors = {
            DRAFT: "#CD6200",
            ACTIVE: "#1F9254",
            CLOSED: "#A30D11",
            ARCHIVED: "#667085",
        };

        const pieItems = items
            .map((i) => ({
                ...i,
                color: colors[i.key] || "#6b7280",
                pct: total > 0 ? (Number(i.value) || 0) / total : 0,
            }))
            .filter((i) => i.value > 0);

        return { items, max, total, pieItems };
    }, [feeReport]);

    const renderFeePie = () => {
        const total = feeStatusSeries.total || 0;

        if (!total) {
            return <div className="fee-pie__empty">Chưa có dữ liệu phí</div>;
        }

        const center = 21;
        const radius = 15.91549430918954;

        let cumulativePct = 0;

        return (
            <div className="fee-pie">
                <div className="fee-pie__chart" role="img" aria-label="Biểu đồ tròn số lượng phí theo trạng thái">
                    <svg viewBox="0 0 42 42" className="fee-pie__svg">
                        <circle
                            className="fee-pie__bg"
                            cx={center}
                            cy={center}
                            r={radius}
                        />

                        {feeStatusSeries.pieItems.map((s) => {
                            const pct = s.pct;
                            const dash = `${(pct * 100).toFixed(3)} ${(100 - pct * 100).toFixed(3)}`;
                            const dashOffset = (25 - cumulativePct * 100).toFixed(3);
                            cumulativePct += pct;

                            return (
                                <circle
                                    key={s.key}
                                    className="fee-pie__slice"
                                    cx={center}
                                    cy={center}
                                    r={radius}
                                    stroke={s.color}
                                    strokeDasharray={dash}
                                    strokeDashoffset={dashOffset}
                                />
                            );
                        })}

                        <text
                            x={center}
                            y={center - 1}
                            textAnchor="middle"
                            className="fee-pie__center-value"
                            fill="#111827"
                        >
                            {formatNumber(total)}
                        </text>
                        <text
                            x={center}
                            y={center + 7}
                            textAnchor="middle"
                            className="fee-pie__center-label"
                            fill="#2563eb"
                        >
                            Tổng phí
                        </text>
                    </svg>
                </div>

                <div className="fee-pie__legend" aria-label="Chú giải trạng thái phí">
                    {feeStatusSeries.items.map((s) => {
                        const pct = total > 0 ? Math.round((Number(s.value || 0) / total) * 100) : 0;
                        const color = feeStatusSeries.pieItems.find((p) => p.key === s.key)?.color || "#6b7280";
                        return (
                            <div className="fee-legend__row" key={s.key}>
                                <span className="fee-legend__dot" style={{ background: color }} />
                                <span className="fee-legend__label">{s.label}</span>
                                <span className="fee-legend__value">
                                    {formatNumber(s.value)} <span className="fee-legend__pct">({pct}%)</span>
                                </span>
                            </div>
                        );
                    })}
                </div>
            </div>
        );
    };

    if (loading) return <div className="overview-loading"><LoadingSpinner /></div>;
    if (error) return <div className="overview-error">{error}</div>;

    return (
        <div className="overview-page">
            <div className="overview-grid">
                <div className="overview-card overview-card--apartment">
                    <div className="overview-card__title">Báo cáo căn hộ</div>
                    <div className="overview-kpi">
                        <div>
                            <div className="overview-kpi__value">{formatNumber(apartmentReport?.resident_total)}</div>
                            <div className="overview-kpi__label">Tổng cư dân</div>
                        </div>
                    </div>
                    <div className="overview-card__meta">
                        <div>
                            Căn hộ: <strong>{formatNumber(apartmentReport?.room_total)}</strong>
                        </div>
                        <div>
                            Tòa nhà: <strong>{formatNumber(apartmentReport?.building_total)}</strong>
                        </div>
                    </div>
                </div>

                <div className="overview-card overview-card--fee">
                    <div className="overview-card__title">Báo cáo phí</div>
                    <div className="overview-subtitle">
                        Tổng đã thu: <strong>{formatVND(feeReport?.total_paid_fee_amount)}</strong>
                    </div>

                    <div className="overview-subtitle">Số lượng phí theo trạng thái</div>
                    {renderFeePie()}
                </div>
            </div>
        </div>
    );
}
