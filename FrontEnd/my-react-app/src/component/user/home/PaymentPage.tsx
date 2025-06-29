import React, { useEffect, useState } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import "../../../style/Home/PaymentPage.scss";

const PaymentPage = () => {
  const [searchParams] = useSearchParams();
  const [status, setStatus] = useState<"loading" | "success" | "failed">(
    "loading"
  );
  const navigate = useNavigate();

  useEffect(() => {
    const confirmPayment = async () => {
      const token = localStorage.getItem("access_token");
      try {
        const query = new URLSearchParams(window.location.search);
        const response = await fetch(
          `http://localhost:8080/api/v1/vn-pay-callback?${query.toString()}`,
          {
            method: "GET",
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );

        if (response.ok) {
          setStatus("success");
          setTimeout(() => navigate("/home"), 3000);
        } else {
          setStatus("failed");
        }
      } catch (error) {
        setStatus("failed");
      }
    };

    confirmPayment();
  }, [navigate]);

  return (
    <div className="payment-landing">
      {status === "loading" && (
        <>
          <div className="loader" />
          <h2>Đang xác nhận thanh toán...</h2>
        </>
      )}

      {status === "success" && (
        <h2 className="success">
          {" "}
          Thanh toán thành công! Đang chuyển hướng...
        </h2>
      )}
      {status === "failed" && (
        <h2 className="failed"> Thanh toán thất bại. Vui lòng thử lại.</h2>
      )}
    </div>
  );
};

export default PaymentPage;
