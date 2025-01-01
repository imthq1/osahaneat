import { CodeOutlined, MailOutlined } from "@ant-design/icons";
import { Input, Button, message } from "antd";
import { useState } from "react";
import { loginAPI } from "../../../api/user.login";
import "../../../style/login.scss";
import { Link, useNavigate } from "react-router";

const Login = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();

  const LoginFetch = async () => {
    try {
      const response = await loginAPI.login(email, password);

      message.success("Login successful!");
      if (response.data.userLogin.role == null) {
        navigate("/home");
      } else if (response.data.userLogin.role.roleName == "admin") {
        navigate("/admin");
      } else {
        navigate("/home");
      }

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

        <Button
          type="primary"
          block
          className="login-button google-login"
          onClick={() => loginAPI.loginGoogle()} // Gọi phương thức loginGoogle
        >
          GOOGLE
        </Button>
        <div className="extra-links">
          <Link to="/forgot" className="forgot-password">
            Forgot password?
          </Link>
          <Link to="/register" className="create-account-button">
            CREATE AN ACCOUNT
          </Link>
        </div>
      </div>
    </div>
  );
};

export default Login;
