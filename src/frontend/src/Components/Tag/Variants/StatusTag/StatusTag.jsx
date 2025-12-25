import "./StatusTag.css";

const statusClassMap = {
    draft: "status-draft",
    active: "status-active",
    closed: "status-closed",
    archived: "status-archived"
};

export default function StatusTag({ status, className = "", children }) {
    const statusClassName = statusClassMap[status];

    if (!statusClassName) {
        console.warn(`StatusTag: trạng thái "${status}" không tồn tại`);
        return null;
    }

    return (
        <span className={`${statusClassName} ${className}`}>
            {children}
        </span>
    );
}
