import FeeTag from "./Variants/FeeTag/FeeTag";
import StatusTag from "./Variants/StatusTag/StatusTag";
import './Tag.css'
import { element } from "prop-types";

const TAG_VARIANTS = {
    Fee: {element: FeeTag, className: "fee"},
    Status: {element: StatusTag, className: "status"}
};

export default function Tag({ variant, type, status, className = "", children }) {
    const {element: Variant, className: variantClassName} = TAG_VARIANTS[variant];

    if (!Variant) {
        console.warn(`Variant "${variant}" không tồn tại`);
        return null;
    }

    return (
        <Variant type={type} status={status} className={`tag ${variantClassName} ${className}`}>
            <div className="parent">
                {children}
            </div>
        </Variant>
    );
}
