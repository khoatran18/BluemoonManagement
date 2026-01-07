import React, { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom"
import "./Sidebar.css";
import bluemoonLogo from "../../assets/logo/bluemoon_logo.svg";
import sidebarAnnouncement from "../../assets/icon/sidebar/sidebar_announcement.svg";
import sidebarApartment from "../../assets/icon/sidebar/sidebar_apartment.svg";
import sidebarFee from "../../assets/icon/sidebar/sidebar_fee.svg";
import sidebarOverview from "../../assets/icon/sidebar/sidebar_overview.svg";
import sidebarPay from "../../assets/icon/sidebar/sidebar_pay.svg";
import { FiChevronDown } from "react-icons/fi";
import { getCurrentRole, ROLE } from "../../auth/authInfo";

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
  citizen: {
    myApartment: "my-apartment",
    myFeeStatus: "my-fee-status",
  },
  announcement: "announcement",
  pay: "pay"
}

const Sidebar = () => {
  const location = useLocation();
  const [openApartmentMenu, setOpenApartmentMenu] = useState(false);
  const [openFeeMenu, setOpenFeeMenu] = useState(false);

  const [sidebarOpen, setSidebarOpen] = useState(false);

  const activeRouteKey = (location.pathname || "/").split("/")[1] || SidebarNav.overview;
  const isApartmentRoute = Object.values(SidebarNav.apartment).includes(activeRouteKey);
  const isFeeRoute = Object.values(SidebarNav.fee).includes(activeRouteKey);

  const role = getCurrentRole();
  const isCitizen = role === ROLE.Citizen;
  const isCollector = role === ROLE.FeeCollector;
  const isAdmin = role === ROLE.Admin;

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
          {isCitizen ? (
            <>
              <SidebarItem
                icon={sidebarApartment}
                label="Thông tin căn hộ"
                to={`/${SidebarNav.citizen.myApartment}`}
                active={activeRouteKey === SidebarNav.citizen.myApartment}
                onClick={() => {
                  setSidebarOpen(false);
                }}
              />

              <SidebarItem
                icon={sidebarFee}
                label="Tình trạng phí"
                to={`/${SidebarNav.citizen.myFeeStatus}`}
                active={activeRouteKey === SidebarNav.citizen.myFeeStatus}
                onClick={() => {
                  setSidebarOpen(false);
                }}
              />
            </>
          ) : (
            <>
              <SidebarItem
                icon={sidebarOverview}
                label="Tổng quan"
                to={`/${SidebarNav.overview}`}
                active={activeRouteKey === SidebarNav.overview}
                onClick={() => {
                  setSidebarOpen(false);
                }}
              />

              {isAdmin ? (
                <SidebarDropdown
                  icon={sidebarApartment}
                  label="Quản lý chung cư"
                  open={openApartmentMenu || isApartmentRoute}
                  onToggle={() => setOpenApartmentMenu(!openApartmentMenu)}>
                  <SidebarItem
                    label="Căn hộ"
                    to={`/${SidebarNav.apartment.apartment}`}
                    active={activeRouteKey === SidebarNav.apartment.apartment}
                    onClick={() => {
                      setSidebarOpen(false);
                    }}
                  />

                  <SidebarItem
                    label="Cư dân"
                    to={`/${SidebarNav.apartment.resident}`}
                    active={activeRouteKey === SidebarNav.apartment.resident}
                    onClick={() => {
                      setSidebarOpen(false);
                    }}
                  />
                </SidebarDropdown>
              ) : null}

              {(isAdmin || isCollector) ? (
                <SidebarDropdown
                  icon={sidebarFee}
                  label="Quản lý phí"
                  open={openFeeMenu || isFeeRoute}
                  onToggle={() => setOpenFeeMenu(!openFeeMenu)}>
                  <SidebarItem
                    label="Danh sách phí"
                    to={`/${SidebarNav.fee.management}`}
                    active={activeRouteKey === SidebarNav.fee.management}
                    onClick={() => {
                      setSidebarOpen(false);
                    }}
                  />

                  <SidebarItem
                    label="Thu phí"
                    to={`/${SidebarNav.fee.collection}`}
                    active={activeRouteKey === SidebarNav.fee.collection}
                    onClick={() => {
                      setSidebarOpen(false);
                    }}
                  />
                </SidebarDropdown>
              ) : null}
            </>
          )}
        </nav>
      </div>
    </>
  );
};

export default Sidebar;
