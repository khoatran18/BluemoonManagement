import React from 'react';
import './Header.css';
import GlobalSearch from '../Search/GlobalSearch';
import Notification from '../Notification/Notification';

const Header = ({ title }) => {
  return (
    <div className="main-header">
      {/* Phần bên trái: Tiêu đề trang */}
      <div className="header-left">
        <h1 className="page-title">{title || "Trang chủ"}</h1>
      </div>

      {/* Phần bên phải: Tìm kiếm, Thông báo, Avatar */}
      <div className="header-right">
        <GlobalSearch />
        
        <Notification />
        
        {/* Avatar người dùng */}
        <div className="user-avatar">
          {/* Bạn thay link ảnh thật vào đây */}
          <img src="https://i.pravatar.cc/40?img=3" alt="User Avatar" />
        </div>
      </div>
    </div>
  );
};

export default Header;