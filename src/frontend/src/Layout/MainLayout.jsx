import Sidebar from "../Components/SideBar/Sidebar";
import Header from "../Components/Header/Header"; // 1. Import Header
import { Outlet, useLocation } from "react-router-dom"; // 2. Import useLocation
import Footer from "../Components/Footer/Footer";

export default function MainLayout() {
  const location = useLocation();

  // 3. Hàm kiểm tra đường dẫn để đặt tên Tiêu đề
  const getPageTitle = (path) => {
    if (path.includes('/apartment')) return 'Quản lý căn hộ ';
    if (path.includes('/resident')) return 'Quản lý cư dân';
    if (path.includes('/fee')) return 'Quản lý phí';
    if (path.includes('/my-apartment')) return 'Thông tin căn hộ';
    if (path.includes('/my-fee-status')) return 'Tình trạng phí';
    // Thêm các case khác tùy ý
    return 'Tổng quan';
  };

  return (
    <div style={{ display: "flex", height: "100vh", overflow: "hidden" }}>
      {/* Sidebar giữ nguyên bên trái */}
      <Sidebar />

      {/* Phần bên phải: Chuyển thành flex column để xếp dọc */}
      <div style={{ flex: 1, display: "flex", flexDirection: "column" }}>
        
        {/* Header nằm trên cùng */}
        <Header title={getPageTitle(location.pathname)} />

        {/* Khu vực nội dung chính (Outlet) */}
        {/* Thêm overflow-y: auto để chỉ cuộn phần nội dung này, Header đứng yên */}
        <div style={{ 
          flex: 1, 
          overflowY: "auto", 
          position: "relative" 
        }}>
          <Outlet />
        </div>

        <Footer />

      </div>
    </div>
  );
}