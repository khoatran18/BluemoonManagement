import './App.css';
import { BrowserRouter, Routes, Route } from "react-router-dom";
import ComingSoon from './Layout/ComingSoon/ComingSoon';
import Overview from './Layout/Overview/Overview';
import MainLayout from './Layout/MainLayout';
<<<<<<< HEAD
import ApartmentManagement from './Layout/Apartment/ApartmentManagement.jsx';
import ResidentManagement from './Layout/Resident/ResidentManagement.jsx';
=======
import Apartment from './Layout/Apartment/Apartment';
import Fee from './Layout/Fee/Fee';
>>>>>>> f023443129220ddaa4948fdd083a9151b023ff7d

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<MainLayout />}>
          <Route index element={<Overview />} />  
          <Route path="overview" element={<ComingSoon />} />
<<<<<<< HEAD
          <Route path="apartment" element={<ApartmentManagement />} />
          <Route path="resident" element={<ResidentManagement />} />
          <Route path="fee" element={<ComingSoon />}/>
=======
          <Route path="apartment" element={<Apartment />} />
          <Route path="resident" element={<ComingSoon />} />
          <Route path="fee" element={<Fee />}/>
>>>>>>> f023443129220ddaa4948fdd083a9151b023ff7d
          <Route path="announcement" element={<ComingSoon />} />
          <Route path="pay" element={<ComingSoon />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
