import './Button.css'

export default function Button({ icon, className = "", children, onClick, disabled = false, type = "button", ...rest }) {
  return (
    <button
      className={`${className} primary-btn`}
      onClick={onClick}
      disabled={disabled}
      type={type}
      {...rest}
    >
      <span className="icon">{icon}</span>
      {children}
    </button>
  );
}
