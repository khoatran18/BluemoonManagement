import React, { useState, useEffect, useRef } from "react";
import "./ActionMenu.css";
import ViewIcon from "../../assets/icon/action/view.svg";
import EditIcon from "../../assets/icon/action/edit.svg";
import DeleteIcon from "../../assets/icon/action/delete.svg";

const iconMap = {
  view: ViewIcon,
  edit: EditIcon,
  delete: DeleteIcon,
};


export const ActionMenu = ({ recordId, actions = [] }) => {
  const [isOpen, setIsOpen] = useState(false);
  const [menuPos, setMenuPos] = useState({ x: 0, y: 0 });
  const menuRef = useRef(null);

  const handleActionClick = (e) => {
    e.stopPropagation();
    // Get mouse position for menu
    setMenuPos({ x: e.clientX, y: e.clientY });
    setIsOpen(!isOpen);
  };

  const handleMenuItemClick = (action) => {
    if (action.onClick) {
      action.onClick();
    }
    setIsOpen(false);
  };

  // Close menu when clicking outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (menuRef.current && !menuRef.current.contains(event.target)) {
        setIsOpen(false);
      }
    };
    if (isOpen) {
      document.addEventListener("mousedown", handleClickOutside);
      return () => document.removeEventListener("mousedown", handleClickOutside);
    }
  }, [isOpen]);

  return (
    <div className="action-menu-container" ref={menuRef}>
      <div
        className="action-button"
        onClick={handleActionClick}
      >
        ⋮
      </div>
      {isOpen && (
        <div
          className="action-dropdown"
          style={{
            position: 'fixed',
            left: menuPos.x + 4,
            top: menuPos.y + 4,
            zIndex: 9999
          }}
        >
          {actions.map((action, index) => (
            <div
              key={index}
              className={`action-item action-item-${action.type || "default"}`}
              onClick={(e) => {
                e.stopPropagation();
                handleMenuItemClick(action);
              }}
            >
              <span>{action.label}</span>
              {action.icon && (
                <img
                  src={iconMap[action.icon]}
                  alt={action.label}
                  className="action-icon"
                />
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
