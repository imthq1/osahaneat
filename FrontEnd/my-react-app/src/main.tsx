import { StrictMode, useEffect, useState } from "react";
import { createRoot } from "react-dom/client";
import ManagerUser from "./screen/user.manage.tsx";
import App from "./App.tsx";
import {
  createBrowserRouter,
  Outlet,
  RouterProvider,
  Link,
  Route,
  Routes,
} from "react-router";
import type { MenuProps } from "antd";
import { HomeOutlined, TeamOutlined } from "@ant-design/icons";
import { Menu } from "antd";
import Login from "./screen/user/login.tsx";

import Reset from "./screen/user/resetPass.tsx";
import Forgot from "./screen/user/forgot.tsx";
import Register from "./screen/user/register.tsx";
import VerifyEmail from "./component/user/login/user.verify.tsx";
import LayoutHome from "./component/user/home/LayoutHome.tsx";
import { CartProvider } from "./context/CartContext"; // đường dẫn đúng theo dự án của bạn
import Home from "./component/user/home/Home.tsx";
import logo from "./img/Remove-bg.ai_1734235132974.png";
import SellerComponent from "./component/user/sales/Seller.tsx";
import RegisterSeller from "./component/user/sales/Register.tsx";
import AdminSeller from "./component/admin/admin.seller.regis.tsx";
import Explore from "./component/user/home/Explore.tsx";
import GoogleCallback from "./component/user/login/GoogleCallback .tsx";
import SellerDashboard from "./component/user/sales/SellerDashboard.tsx";
import FoodList from "./component/user/sales/FoodList.tsx";
import RestaurantDetail from "./component/user/home/RestaurantDetail.tsx";
import CartPage from "./component/user/home/CartPage.tsx";
import PaymentPage from "./component/user/home/PaymentPage.tsx";
const items: MenuProps["items"] = [
  {
    label: <Link to="/admin">Home</Link>,
    key: "home",
    icon: <HomeOutlined />,
  },
  {
    label: <Link to="/admin/user">Manage Users</Link>,
    key: "users",
    icon: <TeamOutlined />,
    disabled: false,
  },
  {
    label: <Link to="/admin/sales">Restaurant registration</Link>,
    key: "register",
    disabled: false,
    icon: <HomeOutlined />,
  },
];
const Header = () => {
  const [current, setCurrent] = useState("home");

  const onClick: MenuProps["onClick"] = (e) => {
    setCurrent(e.key);
  };

  return (
    <>
      <Menu
        onClick={onClick}
        style={{ width: 256 }}
        selectedKeys={[current]}
        mode="inline"
        items={items}
        className="menu-list"
      />
    </>
  );
};

const LayoutAdmin = () => {
  return (
    <div className="layout-container">
      <div className="sidebar-container">
        <a href="http://localhost:5173/home">
          <div className="logo-container">
            <img src={logo} alt="Logo" />;
          </div>
        </a>

        <Header />
      </div>

      <div className="main-content-container">
        <Outlet />
      </div>
    </div>
  );
};
const router = createBrowserRouter([
  {
    path: "/admin",
    element: <LayoutAdmin />,
    children: [
      { index: true, element: <App /> },
      { path: "user", element: <ManagerUser /> },
      { path: "sales", element: <AdminSeller /> },
    ],
  },
  {
    path: "/seller",
    element: <SellerDashboard />,
    children: [{ path: "foods", element: <FoodList /> }],
    //   { path: "foods/add", element: <AddFood /> },
    //   { path: "profile", element: <SellerProfile /> },
    // ],
  },
  { path: "/seller/register", element: <RegisterSeller /> },
  { path: "/payment", element: <PaymentPage /> },

  { path: "/login", element: <Login /> },
  { path: "/login/google", element: <GoogleCallback /> },
  { path: "/forgot", element: <Forgot /> },
  { path: "/forgot/reset", element: <Reset /> },
  { path: "/register", element: <Register /> },
  { path: "/verify", element: <VerifyEmail /> },
  {
    path: "/home",
    element: <LayoutHome />,
    children: [
      { index: true, element: <Home /> },
      { path: "explore", element: <Explore /> },
      {
        path: "restaurants/:id",
        element: <RestaurantDetail />,
      },
      {
        path: "cart",
        element: <CartPage />,
      },
    ],
  },
]);

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <CartProvider>
      <RouterProvider router={router} />
    </CartProvider>
  </StrictMode>
);
