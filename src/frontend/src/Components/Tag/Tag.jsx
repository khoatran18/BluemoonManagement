import FeeTag from "./Variants/FeeTag/FeeTag";
import StatusTag from "./Variants/StatusTag/StatusTag";
import DangerTag from "./Variants/DangerTag/DangerTag";
import SuccessTag from "./Variants/SuccessTag/SuccessTag";
import './Tag.css'

const TAG_VARIANTS = {
    Fee: {element: FeeTag, className: "fee"},
    Status: {element: StatusTag, className: "status"},
    Danger: {element: DangerTag, className: "danger"},
    Success: {element: SuccessTag, className: "success"}
};

export default function Tag({ variant, type, status, className = "", children }) {
    const entry = TAG_VARIANTS[variant];
    if (!entry) {
        console.warn(`Variant "${variant}" không tồn tại`);
        return null;
    }

    const {element: Variant, className: variantClassName} = entry;

    return (
        <Variant type={type} status={status} className={`tag ${variantClassName} ${className}`}>
            <div className="parent">
                {children}
            </div>
        </Variant>
    );
}
