import { useState } from "react";
import "../../../style/registerSeller.scss";
import {
  Button,
  Dropdown,
  Input,
  MenuProps,
  notification,
  Upload,
  UploadProps,
} from "antd";
import { Link, useNavigate } from "react-router";
import { UploadOutlined, UserOutlined } from "@ant-design/icons";
import { loginAPI } from "../../../api/user.login";
import { upload } from "../../../api/api.upload";

const RegisterSeller = () => {
  const token = localStorage.getItem("access_token");
  const navigate = useNavigate();
  const [name, setName] = useState("");
  const [address, setAddress] = useState("");
  const [description, setDescription] = useState("");
  const [logo, setLogo] = useState<string | null>(null);
  const [tempLogo, setTempLogo] = useState<File | null>(null);

  const handleLogout = async () => {
    await loginAPI.logout(navigate);
  };

  const uploadProps: UploadProps = {
    maxCount: 1,
    beforeUpload: (file) => {
      setTempLogo(file);
      notification.success({
        message: "File đã sẵn sàng để đăng ký.",
        description: `${file.name} đã được chọn.`,
      });
      return false;
    },
    onRemove: () => {
      setTempLogo(null);
      notification.info({
        message: "File đã được xóa.",
      });
    },
  };

  const handleButton = async (e: React.FormEvent<HTMLButtonElement>) => {
    e.preventDefault();

    if (!name || !address || !description) {
      notification.error({
        message: "Missing Fields",
        description: "Please fill in all required fields.",
      });
      return;
    }

    let url = null;

    if (tempLogo) {
      try {
        const response = await upload.uploadImage(
          tempLogo,
          "register-logo-images"
        );
        url = response.data.url;
        console.log("Uploaded Logo URL: ", url);
      } catch (error) {
        notification.error({
          message: "Image Upload Failed",
          description: "Unable to upload the image. Please try again.",
        });
        return;
      }
    }

    const newRestaurant = {
      name,
      address,
      description,
      logo: url,
    };

    try {
      const response = await fetch("http://localhost:8080/api/v1/restaurants", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(newRestaurant),
      });

      if (response.ok) {
        notification.success({
          message: "Restaurant Registered Successfully",
          description:
            "The restaurant has been registered. Please wait for admin approval.",
        });

        setName("");
        setAddress("");
        setDescription("");
        setLogo(null);
        setTempLogo(null);
      } else {
        const result = await response.json();
        notification.error({
          message: "Registration Failed",
          description: result.message || "An error occurred while registering.",
        });
      }
    } catch (error) {
      notification.error({
        message: "Network Error",
        description:
          "Unable to register the restaurant. Please try again later.",
      });
    }
  };

  // Dropdown menu for profile
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
          onClick={handleLogout}
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
    <div className="register-container">
      <div className="header">
        <div className="header-logo">
          <span className="title">Đăng ký Nhà hàng Osanhaneat</span>
        </div>
        <Dropdown menu={{ items: profileMenu }} trigger={["click"]}>
          <UserOutlined className="icon" style={{ cursor: "pointer" }} />
        </Dropdown>
      </div>
      <div className="register-form">
        <h2>Form Đăng Ký Nhà Hàng</h2>
        <form>
          <div className="form-group">
            <label className="form-label">Name </label>
            <Input
              placeholder="Restaurant Name"
              value={name}
              onChange={(e) => setName(e.target.value)}
            />
          </div>
          <div className="form-group">
            <label className="form-label">Address </label>
            <input
              type="text"
              name="address"
              value={address}
              onChange={(e) => setAddress(e.target.value)}
              placeholder="Nhập địa chỉ"
              required
            />
          </div>
          <div className="form-group">
            <label>Mô tả</label>
            <textarea
              name="description"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="Nhập mô tả"
              rows={3}
              required
            />
          </div>
          <div className="form-group">
            <label>Logo</label>
            <Upload {...uploadProps}>
              <Button icon={<UploadOutlined />}>Click to Upload</Button>
            </Upload>
          </div>

          <div className="form-actions">
            <button
              type="submit"
              className="submit-button"
              onClick={handleButton}
            >
              Đăng Ký
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default RegisterSeller;
