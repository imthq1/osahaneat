import { useEffect, useState } from "react";
import { Link, Outlet, useNavigate } from "react-router";
import { Badge, Dropdown, Menu } from "antd";
import {
  HomeOutlined,
  TeamOutlined,
  HeartOutlined,
  ShoppingCartOutlined,
  MessageOutlined,
  SettingOutlined,
  FileOutlined,
  UserOutlined,
  ArrowLeftOutlined,
} from "@ant-design/icons";
import type { MenuProps } from "antd";
import { userApi } from "../../../api/user.api";
import { loginAPI } from "../../../api/user.login";
import "../../../style/LayoutHome.scss";
import logo from "../../../img/Remove-bg.ai_1734235132974.png";
import { useCart } from "../../../context/CartContext";
const LayoutHome = () => {
  const [fullName, setfullName] = useState("");
  const [email, setEmail] = useState("");
  const [address, setAddress] = useState("");
  const { totalItems } = useCart();
  const navigate = useNavigate();

  const handleChangeAccount = async () => {
    try {
      const token = localStorage.getItem("access_token");
      if (!token) {
        console.error("Token not found");
        return;
      }

      const data = await userApi.getAccount(token);
      if (data.statusCode === 200 && data.data) {
        setfullName(data.data.fullname || "Unknown User");
        setEmail(data.data.email || "Unknown Email");
        setAddress(data.data.address || "Unknown Address");
      } else {
        console.error("Failed to fetch account details:", data.message);
      }
    } catch (error) {
      console.error("Error fetching account details:", error);
    }
  };

  const handleLogout = async (navigate: any) => {
    await loginAPI.logout(navigate);
  };

  useEffect(() => {
    handleChangeAccount();
  }, []);

  const [current, setCurrent] = useState("home");

  const menuItems: MenuProps["items"] = [
    {
      label: <Link to="/home">Home</Link>,
      key: "home",
      icon: <HomeOutlined />,
    },
    {
      label: <Link to="/home/explore">Explore</Link>,
      key: "explore",
      icon: <TeamOutlined />,
    },
    {
      label: <Link to="/favourites">Favourites</Link>,
      key: "favourites",
      icon: <HeartOutlined />,
    },
    {
      label: <Link to="/orders">Orders</Link>,
      key: "orders",
      icon: <ShoppingCartOutlined />,
    },
    {
      label: <Link to="/messages">Messages</Link>,
      key: "messages",
      icon: <MessageOutlined />,
    },
    {
      label: <Link to="/settings">Settings</Link>,
      key: "settings",
      icon: <SettingOutlined />,
    },
    {
      label: <Link to="/extra">Extra Pages</Link>,
      key: "extra",
      icon: <FileOutlined />,
    },
  ];

  const handleClick: MenuProps["onClick"] = (e) => {
    setCurrent(e.key);
  };

  const Header = () => {
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
        key: "sales-registration",
        label: <Link to="/seller">Sales Registration</Link>,
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
      <div className="header-container">
        <div className="header-left">
          <h3>{address}</h3>
          <div className="header-options">
            <span>Pick up</span> | <span>Best Deals</span>
          </div>
        </div>
        <div className="header-right">
          <input
            type="text"
            placeholder="Search for restaurants, cuisines..."
            className="search-bar"
          />
          <div className="header-icons">
            <Link to="/home/cart">
              <Badge count={totalItems} offset={[0, 5]} size="small" showZero>
                <ShoppingCartOutlined className="icon" />
              </Badge>
            </Link>
            <Dropdown menu={{ items: profileMenu }} trigger={["click"]}>
              <UserOutlined className="icon" style={{ cursor: "pointer" }} />
            </Dropdown>
          </div>
        </div>
      </div>
    );
  };

  return (
    <div className="layout-container">
      {/* Sidebar */}
      <div className="sidebar-container">
        <a href="/home">
          <div className="logo-container">
            <img src={logo} alt="Logo" />
          </div>
        </a>

        <Menu
          onClick={handleClick}
          selectedKeys={[current]}
          mode="inline"
          items={menuItems}
          className="menu-list"
        />

        <div className="sidebar-footer">
          <div className="user-info">
            <UserOutlined className="user-icon" />
            <div>
              <div>{fullName || ""}</div>
              <small>{email || ""}</small>
            </div>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="main-content">
        {/* Header */}
        <Header />
        <div className="main-content-container">
          <Outlet />
        </div>
      </div>
    </div>
  );
};

export default LayoutHome;
