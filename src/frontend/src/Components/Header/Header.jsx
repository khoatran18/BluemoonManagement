import React from 'react';
import './Header.css';
import GlobalSearch from '../Search/GlobalSearch';
import Notification from '../Notification/Notification';

const Header = ({ title }) => {
  return (
    <div className="main-header">
      <div className="header-left">
        <h1 className="page-title">{title || "Trang chủ"}</h1>
      </div>

      <div className="header-right">
        <GlobalSearch />
        
        <Notification />
        
        <div className="user-avatar">
          <img src="https://i.pravatar.cc/40?img=3" alt="User Avatar" />
        </div>
      </div>
    </div>
  );
};

export default Header;