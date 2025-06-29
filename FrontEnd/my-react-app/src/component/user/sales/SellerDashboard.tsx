import React from "react";
import { Outlet, NavLink } from "react-router-dom";
import "../../../style/SellerDashboard.scss";

const SellerDashboard: React.FC = () => {
  return (
    <div className="seller-layout">
      <aside className="sidebar">
        <h2> Seller Panel</h2>
        <nav>
          <ul>
            <li>
              <NavLink
                to="/seller/foods"
                className={({ isActive }) => (isActive ? "active" : "")}
              >
                Quản lý món ăn
              </NavLink>
            </li>
            <li>
              <NavLink
                to="/seller/foods/add"
                className={({ isActive }) => (isActive ? "active" : "")}
              >
                Thêm món mới
              </NavLink>
            </li>
            <li>
              <NavLink
                to="/seller/profile"
                className={({ isActive }) => (isActive ? "active" : "")}
              >
                Hồ sơ
              </NavLink>
            </li>
          </ul>
        </nav>
      </aside>

      <main className="seller-main-content">
        <Outlet />
      </main>
    </div>
  );
};

export default SellerDashboard;
