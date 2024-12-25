import { useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { message } from "antd";
import { loginAPI } from "../../../api/user.login";
import "../../../style/verify.scss";

const VerifyEmail = () => {
  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    const queryParams = new URLSearchParams(location.search);
    const token = queryParams.get("token");

    if (token) {
      loginAPI
        .verifyEmail(token)
        .then(() => {
          message.success("Email verified successfully!");
          navigate("/login");
        })
        .catch((err) => {
          message.error(err.message || "Failed to verify email.");
        });
    } else {
      message.error("Invalid verification token.");
    }
  }, [location, navigate]);

  return (
    <div className="verify-container">
      <div className="spinner"></div>
      <h2>Verifying your email...</h2>
    </div>
  );
};

export default VerifyEmail;
