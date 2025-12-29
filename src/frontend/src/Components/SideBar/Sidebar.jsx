import React, { useState } from "react";
import { useNavigate } from "react-router-dom"
import "./Sidebar.css";
import bluemoonLogo from "../../assets/logo/bluemoon_logo.svg";
import sidebarAnnouncement from "../../assets/icon/sidebar/sidebar_announcement.svg";
import sidebarApartment from "../../assets/icon/sidebar/sidebar_apartment.svg";
import sidebarFee from "../../assets/icon/sidebar/sidebar_fee.svg";
import sidebarOverview from "../../assets/icon/sidebar/sidebar_overview.svg";
import sidebarPay from "../../assets/icon/sidebar/sidebar_pay.svg";
import { FiChevronDown } from "react-icons/fi";

const SidebarItem = ({ icon, label, to, active, onClick }) => {
  const navigate = useNavigate()

  const handleClick = () => {
    navigate(to)
    onClick?.()
  }

  return (
    <button
      className={`sidebar-item ${active ? "active" : ""}`}
      onClick={handleClick}
      type="button"
    >
      {icon && <img src={icon} alt={label} className="sidebar-icon" />}
      <span>{label}</span>
    </button>
  );
};

const SidebarDropdown = ({ icon, label, open, onToggle, children }) => {
  return (
    <div className="sidebar-section">
      <button onClick={onToggle} className="sidebar-dropdown-btn">
        <div className="sidebar-dropdown-left">
          <img src={icon} alt={label} className="sidebar-icon" />
          <span>{label}</span>
        </div>

        <FiChevronDown
          className={`sidebar-dropdown-icon ${open ? "rotate" : ""}`}
        />
      </button>

      <div className={`sidebar-submenu ${open ? "open" : ""}`}>
        {children}
      </div>
    </div>
  );
};

const SidebarNav = {
  overview: "overview",
  apartment: {
    apartment: "apartment",
    resident: "resident"
  },
   fee: {
    management: "fee",         
    collection: "fee-collection" 
  },
  announcement: "announcement",
  pay: "pay"
}

const Sidebar = () => {
  const [openApartmentMenu, setOpenApartmentMenu] = useState(false);
  const [openFeeMenu, setOpenFeeMenu] = useState(false);
  const [activeItem, setActiveItem] = useState(SidebarNav.overview)

  const [sidebarOpen, setSidebarOpen] = useState(false);

  return (
    <>
     
      <button className="sidebar-toggle" onClick={() => setSidebarOpen(true)}>
        ☰
      </button>

      <div
        className={`sidebar-overlay ${sidebarOpen ? "show" : ""}`}
        onClick={() => setSidebarOpen(false)}
      />

      <div className={`sidebar ${sidebarOpen ? "open" : ""}`}>
        {/* Logo */}
        <div className="sidebar-logo">
          <img src={bluemoonLogo} alt="Bluemoon" />
          <span className="sidebar-logo-text">Bluemoon</span>
        </div>

        {/* Menu */}
        <nav className="sidebar-nav">
          <SidebarItem icon={sidebarOverview} label="Tổng quan"
            to={`/${SidebarNav.overview}`}
            active={activeItem === SidebarNav.overview}
            onClick={() => { setActiveItem(SidebarNav.overview); setSidebarOpen(false) }}
          />

          <SidebarDropdown
            icon={sidebarApartment}
            label="Quản lý chung cư"
            open={openApartmentMenu}
            onToggle={() => setOpenApartmentMenu(!openApartmentMenu)}>
            <SidebarItem label="Căn hộ"
              to={`/${SidebarNav.apartment.apartment}`}
              active={activeItem === SidebarNav.apartment.apartment}
              onClick={() => { setActiveItem(SidebarNav.apartment.apartment); setSidebarOpen(false) }}
            />

            <SidebarItem label="Cư dân"
              to={`/${SidebarNav.apartment.resident}`}
              active={activeItem === SidebarNav.apartment.resident}
              onClick={() => { setActiveItem(SidebarNav.apartment.resident); setSidebarOpen(false) }}
            />
          </SidebarDropdown>

          <SidebarDropdown
            icon={sidebarFee}
            label="Quản lý phí"
            open={openFeeMenu}
            onToggle={() => setOpenFeeMenu(!openFeeMenu)}>
            <SidebarItem label="Danh sách phí"
              to={`/${SidebarNav.fee.management}`}
              active={activeItem === SidebarNav.fee.management}
              onClick={() => { setActiveItem(SidebarNav.fee.management); setSidebarOpen(false) }}
            />

            <SidebarItem label="Thu phí"
              to={`/${SidebarNav.fee.collection}`}
              active={activeItem === SidebarNav.fee.collection}
              onClick={() => { setActiveItem(SidebarNav.fee.collection); setSidebarOpen(false) }}
            />
          </SidebarDropdown>

          <SidebarItem icon={sidebarAnnouncement} label="Thông báo"
            to={`/${SidebarNav.announcement}`}
            active={activeItem === SidebarNav.announcement}
            onClick={() => { setActiveItem(SidebarNav.announcement); setSidebarOpen(false) }}
          />

          <SidebarItem icon={sidebarPay} label="Thanh toán"
            to={`/${SidebarNav.pay}`}
            active={activeItem === SidebarNav.pay}
            onClick={() => { setActiveItem(SidebarNav.pay); setSidebarOpen(false) }}
          />
        </nav>
      </div>
    </>
  );
};

export default Sidebar;
