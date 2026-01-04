import React, { useEffect, useRef, useState } from 'react';
import './Header.css';
import GlobalSearch from '../Search/GlobalSearch';
import Notification from '../Notification/Notification';
import { useNavigate } from 'react-router-dom';
import { clearTokens } from '../../auth/tokenStorage';

const Header = ({ title }) => {
  const navigate = useNavigate();
  const [menuOpen, setMenuOpen] = useState(false);
  const menuRef = useRef(null);

  const onLogout = () => {
    clearTokens();
    setMenuOpen(false);
    navigate('/login', { replace: true });
  };

  useEffect(() => {
    if (!menuOpen) return;

    const onDocMouseDown = (e) => {
      if (!menuRef.current) return;
      if (!menuRef.current.contains(e.target)) {
        setMenuOpen(false);
      }
    };

    document.addEventListener('mousedown', onDocMouseDown);
    return () => document.removeEventListener('mousedown', onDocMouseDown);
  }, [menuOpen]);

  return (
    <div className="main-header">
      <div className="header-left">
        <h1 className="page-title">{title || "Trang chủ"}</h1>
      </div>

      <div className="header-right">
        <GlobalSearch />
        
        <Notification />
        
        <div className="user-avatar" ref={menuRef}>
          <button
            type="button"
            className="user-avatar-button"
            onClick={() => setMenuOpen((v) => !v)}
            aria-label="Tài khoản"
            title="Tài khoản"
            aria-expanded={menuOpen}
          >
            <img src="https://i.pravatar.cc/40?img=3" alt="User Avatar" />
          </button>

          {menuOpen ? (
            <div className="user-menu" role="menu" aria-label="Menu tài khoản">
              <button type="button" className="user-menu-item" onClick={onLogout} role="menuitem">
                Đăng xuất
              </button>
            </div>
          ) : null}
        </div>
      </div>
    </div>
  );
};

export default Header;