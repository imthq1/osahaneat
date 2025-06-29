import React, { useState } from "react";
import { useCart } from "../../../context/CartContext";
import "../../../style/Home/CartPage.scss";

const CartPage = () => {
  const { cart, removeFromCart, clearCart, addToCart } = useCart();
  const [shippingAddress, setShippingAddress] = useState(
    "123 Đường ABC, Quận XYZ, Thành phố PQR"
  );
  const [paymentMethod, setPaymentMethod] = useState("vnpay");
  const [voucherCode, setVoucherCode] = useState("");

  const total = cart.reduce(
    (sum, item) => sum + item.price * item.quantityInCart,
    0
  );

  const handleOrder = async () => {
    const token = localStorage.getItem("access_token");
    const orderRequest = {
      foods: cart.map((item) => ({
        foodId: item.id,
        quantity: item.quantityInCart,
      })),
      shippingAddress,
      paymentMethod,
      voucherCode,
    };

    try {
      const response = await fetch("http://localhost:8080/api/v1/orders", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(orderRequest),
      });

      if (!response.ok) {
        const errorData = await response.text();
        throw new Error(errorData);
      }

      const orderId = await response.json();

      if (paymentMethod === "vnpay") {
        const vnpRes = await fetch(
          `http://localhost:8080/api/v1/vn-pay?OrderID=${orderId.data}`,
          {
            method: "GET",
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );

        if (!vnpRes.ok) {
          const error = await vnpRes.text();
          throw new Error(error);
        }

        const vnpData = await vnpRes.json();
        const vnpayUrl = vnpData.data.data.paymentUrl;

        window.location.href = vnpayUrl;
      } else {
        alert("Đặt hàng thành công. Vui lòng chờ xác nhận!");
        clearCart();
      }
    } catch (error: any) {
      alert("Lỗi khi đặt hàng: " + error.message);
    }
  };

  const increaseQuantity = (item: any) => {
    addToCart({ ...item, quantityInCart: 1 });
  };

  const decreaseQuantity = (item: any) => {
    if (item.quantityInCart > 1) {
      addToCart({ ...item, quantityInCart: -1 });
    } else {
      removeFromCart(item.id);
    }
  };

  return (
    <div className="cart-page">
      <header className="header">
        <div className="logo">Osahaneat</div>
        <h1>Thanh toán</h1>
      </header>
      <div className="payment-content">
        <div className="order-summary">
          <h2>Đơn hàng</h2>
          {cart.length === 0 ? (
            <p>Giỏ hàng trống.</p>
          ) : (
            <table className="cart-table">
              <thead>
                <tr>
                  <th>Tên món</th>
                  <th>Giá</th>
                  <th>Số lượng</th>
                  <th>Tổng</th>
                  <th>Thao tác</th>
                </tr>
              </thead>
              <tbody>
                {cart.map((item) => (
                  <tr key={item.id}>
                    <td>
                      <img
                        src={`https://res.cloudinary.com/dw4yj3kvq/image/upload/v1750081399/${item.logo}`}
                        alt={item.name}
                      />
                      {item.name}
                    </td>
                    <td>{item.price.toLocaleString()} đ</td>
                    <td>
                      <button onClick={() => decreaseQuantity(item)}>-</button>
                      <span style={{ margin: "0 8px" }}>
                        {item.quantityInCart}
                      </span>
                      <button onClick={() => increaseQuantity(item)}>+</button>
                    </td>
                    <td>
                      {(item.price * item.quantityInCart).toLocaleString()} đ
                    </td>
                    <td>
                      <button onClick={() => removeFromCart(item.id)}>
                        Xóa
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
        <div className="shipping-info">
          <h2>Thông tin giao hàng</h2>
          <p>{shippingAddress}</p>
        </div>
        <div className="voucher-section">
          <h2>Mã giảm giá</h2>
          <input
            type="text"
            value={voucherCode}
            onChange={(e) => setVoucherCode(e.target.value)}
            placeholder="Nhập mã giảm giá"
          />
        </div>
        <div className="confirm-order">
          <div className="checkout-bar">
            <div className="left">
              <div className="total-price">
                Tổng cộng: <span>{total.toLocaleString()} đ</span>
              </div>
              <div className="payment-method-inline">
                <label>
                  <input
                    type="radio"
                    name="payment"
                    value="vnpay"
                    checked={paymentMethod === "vnpay"}
                    onChange={() => setPaymentMethod("vnpay")}
                  />
                  VNPay
                </label>
                <label>
                  <input
                    type="radio"
                    name="payment"
                    value="cod"
                    checked={paymentMethod === "cod"}
                    onChange={() => setPaymentMethod("cod")}
                  />
                  COD
                </label>
              </div>
            </div>
            <div className="right">
              <button className="btn-order" onClick={handleOrder}>
                Xác nhận đơn hàng
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CartPage;
