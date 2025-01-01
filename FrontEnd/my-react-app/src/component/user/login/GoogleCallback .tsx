import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { message } from "antd";
import "../../../style/verify.scss";

const GoogleCallback = () => {
  const navigate = useNavigate();

  useEffect(() => {
    const handleGoogleLogin = async () => {
      const urlParams = new URLSearchParams(window.location.search);
      const code = urlParams.get("code");

      if (!code) {
        message.error("Login failed: No authorization code found.");
        navigate("/login");
        return;
      }

      const response = await fetch(
        `http://localhost:8080/api/v1/login/oauth2/code/google?code=${code}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );

      if (!response.ok) {
        const errorData = await response.json();
        message.error(errorData.message || "Failed to login with Google.");
        navigate("/login");
        return;
      }

      const data = await response.json();
      localStorage.setItem("access_token", data.data.access_token);
      message.success("Login successful!");
      navigate("/home");
    };

    handleGoogleLogin();
  }, [navigate]);

  return (
    <>
      <div className="verify-container">
        <div className="spinner"></div>
        <h2>Loading...</h2>
        <div />
      </div>
    </>
  );
};

export default GoogleCallback;
