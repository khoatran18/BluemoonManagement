import "./DangerTag.css";

export default function DangerTag({ className = "", children }) {
    return (
        <span className={`danger-tag ${className}`}>
            {children}
        </span>
    );
}
