import './App.css';
import { BrowserRouter, Routes, Route } from "react-router-dom";
import RequireAuth from './auth/RequireAuth';
import RequireRole from './auth/RequireRole';
import RoleLanding from './auth/RoleLanding';
import { ROLE } from './auth/authInfo';
import ComingSoon from './Layout/ComingSoon/ComingSoon';
import Overview from './Layout/Overview/Overview';
import MainLayout from './Layout/MainLayout';
import ApartmentManagement from './Layout/Apartment/ApartmentManagement.jsx';
import ResidentManagement from './Layout/Resident/ResidentManagement.jsx';
import Fee from './Layout/Fee/Fee';
import FeeCollect from './Layout/FeeCollect/FeeCollect.jsx';
import ApartmentFeeStatus from './Layout/FeeCollect/ApartmentFeeStatus.jsx';
import CollectFee from './Layout/FeeCollect/CollectFee.jsx';
import MyApartment from './Layout/Citizen/MyApartment.jsx';
import MyFeeStatus from './Layout/Citizen/MyFeeStatus.jsx';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import NotFoundPage from './pages/NotFoundPage';

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        <Route path="*" element={<NotFoundPage />} />

        <Route
          path="/"
          element={
            <RequireAuth>
              <MainLayout />
            </RequireAuth>
          }
        >
          <Route index element={<RoleLanding />} />

          <Route
            path="overview"
            element={
              <RequireRole allowedRoles={[ROLE.Admin, ROLE.FeeCollector]}>
                <Overview />
              </RequireRole>
            }
          />

          <Route
            path="apartment"
            element={
              <RequireRole allowedRoles={[ROLE.Admin]}>
                <ApartmentManagement />
              </RequireRole>
            }
          />

          <Route
            path="resident"
            element={
              <RequireRole allowedRoles={[ROLE.Admin]}>
                <ResidentManagement />
              </RequireRole>
            }
          />

          <Route
            path="fee"
            element={
              <RequireRole allowedRoles={[ROLE.Admin, ROLE.FeeCollector]}>
                <Fee />
              </RequireRole>
            }
          />

          <Route
            path="fee-collection"
            element={
              <RequireRole allowedRoles={[ROLE.Admin, ROLE.FeeCollector]}>
                <FeeCollect />
              </RequireRole>
            }
          />

          <Route
            path="fee-collection/apartment/:id/status"
            element={
              <RequireRole allowedRoles={[ROLE.Admin, ROLE.FeeCollector]}>
                <ApartmentFeeStatus />
              </RequireRole>
            }
          />

          <Route
            path="fee-collection/apartment/:id/collect"
            element={
              <RequireRole allowedRoles={[ROLE.Admin, ROLE.FeeCollector]}>
                <CollectFee />
              </RequireRole>
            }
          />

          <Route
            path="my-apartment"
            element={
              <RequireRole allowedRoles={[ROLE.Citizen]}>
                <MyApartment />
              </RequireRole>
            }
          />

          <Route
            path="my-fee-status"
            element={
              <RequireRole allowedRoles={[ROLE.Citizen]}>
                <MyFeeStatus />
              </RequireRole>
            }
          />

          <Route path="*" element={<NotFoundPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
