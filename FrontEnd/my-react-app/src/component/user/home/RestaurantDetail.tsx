import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { StarFilled } from "@ant-design/icons";
import { restaurantAPI } from "../../../api/api.restaurant";
import "../../../style/Home/RestaurantDetail.scss";
import { useCart } from "../../../context/CartContext";
import { message } from "antd";

const RestaurantDetail = () => {
  const { id } = useParams<{ id: string }>();
  const [restaurant, setRestaurant] = useState<any>(null);
  const { addToCart } = useCart();
  useEffect(() => {
    const fetchDetail = async () => {
      if (!id) return;
      try {
        const res = await restaurantAPI.getById(id);
        setRestaurant(res.data);
      } catch (error) {
        console.error("Lỗi khi lấy thông tin nhà hàng:", error);
      }
    };

    fetchDetail();
  }, [id]);

  if (!restaurant) return <p>Đang tải thông tin nhà hàng...</p>;

  const renderStars = (rating: number) => {
    const stars = [];
    for (let i = 1; i <= 5; i++) {
      stars.push(
        <StarFilled
          key={i}
          style={{ color: i <= rating ? "#ffd700" : "#ccc", fontSize: "16px" }}
        />
      );
    }
    return stars;
  };

  return (
    <div className="restaurant-detail">
      <div className="hero">
        <img
          src={
            "https://res.cloudinary.com/dw4yj3kvq/image/upload/v1750081399/" +
            restaurant.logo
          }
          alt={restaurant.name}
        />
        <div className="hero-overlay">
          <h1>{restaurant.name}</h1>
          <p>
            {restaurant.cuisine || "Ẩm thực"} | {restaurant.priceRange || "$$"}
          </p>
        </div>
      </div>
      <div className="details">
        <p>
          <strong>Địa chỉ:</strong> {restaurant.address}
        </p>
        <p>
          <strong>Thời gian mở cửa:</strong>{" "}
          {restaurant.hours || "9:00 - 22:00"}
        </p>
        <p>
          <strong>Liên hệ:</strong> {restaurant.phone || "Chưa có thông tin"}
        </p>
        <div className="rating">
          <span>Đánh giá: {restaurant.rating} / 5 </span>
          {renderStars(restaurant.rating)}
        </div>
      </div>
      <div className="menu">
        <h2>Thực đơn</h2>
        {restaurant.foods && restaurant.foods.length > 0 ? (
          restaurant.foods.map((item: any) => (
            <div key={item.id} className="menu-item">
              <img
                className="food-image"
                src={`https://res.cloudinary.com/dw4yj3kvq/image/upload/v1750081399/${item.logo}`}
                alt={item.name}
              />
              <div className="food-info">
                <h3>{item.name}</h3>
                <p>{item.description}</p>
                <p className="price">{item.price.toLocaleString()} VND</p>
              </div>
              <button
                className="add-button"
                onClick={() => {
                  addToCart(item);
                  message.success("Đã thêm vào giỏ hàng!");
                }}
              >
                +
              </button>
            </div>
          ))
        ) : (
          <p>Chưa có thực đơn.</p>
        )}
      </div>
    </div>
  );
};

export default RestaurantDetail;
