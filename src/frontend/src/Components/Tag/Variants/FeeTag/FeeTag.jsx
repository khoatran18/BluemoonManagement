import "./FeeTag.css";

const typeClassMap = {
    obligatory: "fee-obligatory",
    voluntary: "fee-voluntary",
    impromptu: "fee-impromptu"
};

export default function FeeTag({ type, className = "", children }) {
    const typeClassName = typeClassMap[type];

    if (!typeClassName) {
        console.warn(`FeeTag: loại "${type}" không tồn tại`);
        return null;
    }

    return (
        <span className={`${typeClassName} ${className}`}>
            {children}
        </span>
    );
}
