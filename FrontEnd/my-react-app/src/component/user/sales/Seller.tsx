import React, { useState } from "react";

import { Dropdown, Menu, MenuProps } from "antd";
import { Link, useNavigate } from "react-router";
import { UserOutlined } from "@ant-design/icons";
import { loginAPI } from "../../../api/user.login";
import RegisterSeller from "./Register";
import "../../../style/sales.scss";

const SellerComponent = () => {
  const navigate = useNavigate();
  
  const handleLogout = async (navigate: any) => {
    await loginAPI.logout(navigate);
  };

  // Dropdown menu
  const profileMenu: MenuProps["items"] = [
    {
      key: "profile",
      label: <Link to="/profile">Profile</Link>,
    },
    {
      key: "settings",
      label: <Link to="/settings">Settings</Link>,
    },
    {
      key: "logout",
      label: (
        <button
          onClick={() => {
            handleLogout(navigate);
          }}
          style={{
            background: "none",
            border: "none",
            cursor: "pointer",
            color: "inherit",
          }}
        >
          Logout
        </button>
      ),
    },
  ];

  return (
    <>
      <div className="sales-container">
        {/* Header */}
        <div className="header">
          <div className="header-logo">
            <span className="title">
              Đăng ký trở thành Người bán Osanhaneat
            </span>
          </div>
          <Dropdown menu={{ items: profileMenu }} trigger={["click"]}>
            <UserOutlined className="icon" style={{ cursor: "pointer" }} />
          </Dropdown>
        </div>

        {/* Main Content */}
        <div className="main-content">
          <div className="welcome-section">
            <img
              src="../src/img/depositphotos_666201564-stock-photo-employee-hiring-process-concept-people.jpg"
              alt="Welcome"
              className="welcome-image"
            />
            <h1 className="welcome-title">Chào mừng đến với Osanhaneat!</h1>
            <p className="welcome-description">
              Vui lòng cung cấp thông tin để thành lập tài khoản người bán trên
              Osanhaneat
            </p>
            <Link to={"/seller/register"} className="register-button">
              Bắt đầu đăng ký
            </Link>
          </div>
        </div>
      </div>
      <div className="register"></div>
    </>
  );
};

export default SellerComponent;
