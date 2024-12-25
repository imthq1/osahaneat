import { CodeOutlined } from "@ant-design/icons";
import { Button, Input, message } from "antd";
import { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import "../../../style/login.scss";
const BASE_URL = "http://localhost:8080/api/v1";

const ResetPass = () => {
  const [newPassword, setNewPassword] = useState("");
  const location = useLocation();
  const navigate = useNavigate();
  const queryParams = new URLSearchParams(location.search);
  const token = queryParams.get("token");

  const handleResetPass = async () => {
    if (!token) {
      message.error("Invalid or missing token.");
      return;
    }

    if (!newPassword) {
      message.error("Password cannot be empty!");
      return;
    }

    try {
      const response = await fetch(
        `${BASE_URL}/reset-password?token=${token}&newPassword=${newPassword}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || "Failed to reset password.");
      }

      message.success("Password has been reset successfully!");
      navigate("/login");
    } catch (error: any) {
      message.error(error.message || "Something went wrong!");
    }
  };

  return (
    <>
      <div className="login-container">
        <div className="login-box">
          <h1 className="welcome-title">Reset Password</h1>
          <p className="welcome-subtitle">
            Please enter your new password to continue
          </p>
          <div className="form-group">
            <label className="form-label">New Password</label>
            <Input.Password
              prefix={<CodeOutlined />}
              placeholder="Enter New Password"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              className="form-input"
            />
          </div>
          <Button
            type="primary"
            block
            className="login-button"
            onClick={handleResetPass}
          >
            CONTINUE
          </Button>
        </div>
      </div>
    </>
  );
};

export default ResetPass;
