import { CodeOutlined, MailOutlined } from "@ant-design/icons";
import { Input, Button, message } from "antd";
import { useState } from "react";
import LoginAPI from "../../api/user.login"; // Import API class
import "../../style/login.scss"; // Import SCSS

const Login = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const LoginFetch = async () => {
    try {
      const response = await LoginAPI.login(email, password);

      message.success("Login successful!");

      localStorage.setItem("access_token", response.data.access_token);
    } catch (error) {
      message.error("Invalid email or password. Please try again.");
    }
  };

  return (
    <div className="login-container">
      <div className="login-box">
        <h1 className="welcome-title">Welcome</h1>
        <p className="welcome-subtitle">Sign in to your account to continue</p>

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

        <div className="form-group">
          <label className="form-label">Password</label>
          <Input.Password
            prefix={<CodeOutlined />}
            placeholder="Enter Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="form-input"
          />
        </div>

        <Button
          type="primary"
          block
          className="login-button"
          onClick={LoginFetch}
        >
          SIGN IN
        </Button>

        <div className="extra-links">
          <a href="#" className="forgot-password">
            Forgot password?
          </a>
          <Button type="default" block className="create-account-button">
            CREATE AN ACCOUNT
          </Button>
        </div>
      </div>
    </div>
  );
};

export default Login;
