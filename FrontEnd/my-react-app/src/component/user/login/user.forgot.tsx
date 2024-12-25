import { MailOutlined } from "@ant-design/icons";
import { Button, Input, message } from "antd";
import { useState } from "react";
import { loginAPI } from "../../../api/user.login";
import "../../../style/register.scss";
const ForgotPass = () => {
  const [email, setEmail] = useState("");

  const forgotPass = async () => {
    if (!email) {
      message.error("Email is required!");
      return;
    }
    try {
      const data = await loginAPI.forgot(email);
      message.success("Send link successful!");
    } catch (error) {
      message.error("Failed to send link. Please try again.");
    }
  };

  return (
    <>
      <div className="login-container">
        <div className="login-box">
          <h1 className="welcome-title">Forgot Password?</h1>
          <p className="welcome-subtitle">
            please enter your email address to continue
          </p>
          <div className="form-group">
            <label className="form-label">Email Address</label>
            <Input
              prefix={<MailOutlined />}
              placeholder="Enter Your Email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="form-input"
            />
          </div>
          <Button
            type="primary"
            block
            className="login-button"
            onClick={forgotPass}
          >
            CONTINUE
          </Button>
        </div>
      </div>
    </>
  );
};
export default ForgotPass;
