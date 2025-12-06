import './App.css';
import { BrowserRouter, Routes, Route } from "react-router-dom";
import ComingSoon from './Layout/ComingSoon/ComingSoon';
import Overview from './Layout/Overview/Overview';
import MainLayout from './Layout/MainLayout';
import ApartmentManagement from './Layout/Apartment/ApartmentManagement.jsx';
import ResidentManagement from './Layout/Resident/ResidentManagement.jsx';

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<MainLayout />}>
          <Route index element={<Overview />} />  
          <Route path="overview" element={<ComingSoon />} />
          <Route path="apartment" element={<ApartmentManagement />} />
          <Route path="resident" element={<ResidentManagement />} />
          <Route path="fee" element={<ComingSoon />}/>
          <Route path="announcement" element={<ComingSoon />} />
          <Route path="pay" element={<ComingSoon />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
