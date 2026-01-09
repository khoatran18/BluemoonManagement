import "./SuccessTag.css";

export default function SuccessTag({ className = "", children }) {
    return (
        <span className={`success-tag ${className}`}>
            {children}
        </span>
    );
}
