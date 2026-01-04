import './App.css';
import { BrowserRouter, Routes, Route } from "react-router-dom";
import RequireAuth from './auth/RequireAuth';
import ComingSoon from './Layout/ComingSoon/ComingSoon';
import Overview from './Layout/Overview/Overview';
import MainLayout from './Layout/MainLayout';
import ApartmentManagement from './Layout/Apartment/ApartmentManagement.jsx';
import ResidentManagement from './Layout/Resident/ResidentManagement.jsx';
import Fee from './Layout/Fee/Fee';
import FeeCollect from './Layout/FeeCollect/FeeCollect.jsx';
import ApartmentFeeStatus from './Layout/FeeCollect/ApartmentFeeStatus.jsx';
import CollectFee from './Layout/FeeCollect/CollectFee.jsx';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        <Route
          path="/"
          element={
            <RequireAuth>
              <MainLayout />
            </RequireAuth>
          }
        >
          <Route index element={<Overview />} />  
          <Route path="overview" element={<ComingSoon />} />
          <Route path="apartment" element={<ApartmentManagement />} />
          <Route path="resident" element={<ResidentManagement />} />
          <Route path="fee" element={<Fee />}/>
          <Route path="fee-collection" element={<FeeCollect />}/>
          <Route path="fee-collection/apartment/:id/status" element={<ApartmentFeeStatus />}/>
          <Route path="fee-collection/apartment/:id/collect" element={<CollectFee />}/>
          <Route path="announcement" element={<ComingSoon />} />
          <Route path="pay" element={<ComingSoon />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
