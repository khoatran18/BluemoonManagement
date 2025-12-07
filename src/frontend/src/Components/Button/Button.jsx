import './Button.css'

export default function Button({ icon, className, children, onClick }) {
  return (
    <button className={`${className} primary-btn`} onClick={onClick}>
      <span className="icon">{icon}</span>
      {children}
    </button>
  );
}
