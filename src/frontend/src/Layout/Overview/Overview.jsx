import { useEffect, useMemo, useState } from "react";
import "./Overview.css";
import { getApartmentCommonReport, getFeeCommonReport } from "../../api/reportApi";

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
        return { items, max };
    }, [feeReport]);

    if (loading) return <div className="overview-loading">Đang tải tổng quan...</div>;
    if (error) return <div className="overview-error">{error}</div>;

    return (
        <div className="overview-page">
            <div className="overview-grid">
                <div className="overview-card">
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

                <div className="overview-card">
                    <div className="overview-card__title">Báo cáo phí</div>
                    <div className="overview-subtitle">
                        Tổng đã thu: <strong>{formatVND(feeReport?.total_paid_fee_amount)}</strong>
                    </div>

                    <div className="overview-subtitle">Số lượng phí theo trạng thái</div>
                    <div className="status-bars" role="img" aria-label="Biểu đồ số lượng phí theo trạng thái">
                        {feeStatusSeries.items.map((item) => {
                            const pct = Math.round((item.value / feeStatusSeries.max) * 100);
                            return (
                                <div className="status-bar" key={item.key}>
                                    <div className="status-bar__label">{item.label}</div>
                                    <div className="status-bar__track">
                                        <div className="status-bar__fill" style={{ width: `${pct}%` }} />
                                    </div>
                                    <div className="status-bar__value">{formatNumber(item.value)}</div>
                                </div>
                            );
                        })}
                    </div>
                </div>
            </div>
        </div>
    );
}
