import { StrictMode, useEffect, useState } from "react";
import { createRoot } from "react-dom/client";
import ManagerUser from "./screen/user.manage.tsx";
import "./style/tableUser.scss";
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
import { FireOutlined, TeamOutlined } from "@ant-design/icons";
import { Menu } from "antd";
import Login from "./screen/user/login.tsx";
import DisplayLogin from "./screen/user/login.tsx";

const items: MenuProps["items"] = [
  {
    label: <Link to="/admin">Home</Link>,
    key: "home",
    icon: <FireOutlined />,
  },
  {
    label: <Link to="/admin/user">Manage Users</Link>,
    key: "users",
    icon: <TeamOutlined />,
    disabled: false,
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
  const fetchData = async () => {
    try {
      const response = await fetch("http://localhost:8080/api/v1/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          username: "xamthang09@gmail.com",
          password: "123",
        }),
      });
      const data = await response.json();
      localStorage.setItem("access_token", data.data.access_token);
    } catch (error) {
      console.error("Error fetching users:", error);
    }
  };
  useEffect(() => {
    fetchData();
  }, []);
  return (
    <div className="layout-container">
      <div className="sidebar-container">
        <a href="http://localhost:5173/admin">
          <div className="logo-container">
            <img src="src/img/Remove-bg.ai_1734235132974.png" alt="Logo" />
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
      {
        path: "user",
        element: <ManagerUser />,
      },
    ],
  },
  {
    path: "/login",
    element: <DisplayLogin />,
  },
]);

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <RouterProvider router={router}></RouterProvider>
  </StrictMode>
);
