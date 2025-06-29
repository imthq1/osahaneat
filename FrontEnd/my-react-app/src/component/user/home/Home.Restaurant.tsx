import { useEffect, useState } from "react";
import { restaurantAPI } from "../../../api/api.restaurant";
import { IRestaurant } from "../../../type/restaurant.type";
import { notification } from "antd";
import { upload } from "../../../api/api.upload";
import "../../../style/Home/home.scss";

const ApprovedRestaurants = () => {
  const [restaurants, setRestaurants] = useState<IRestaurant[]>([]);

  const getAllApproved = async () => {
    try {
      const response = await restaurantAPI.getAllApproved();

      if (response.statusCode === 200) {
        const restaurantsWithImages = await Promise.all(
          response.data.result.map(async (restaurant: IRestaurant) => {
            const imageUrl = await upload.loadImage(
              restaurant.id,
              "restaurant-logo"
            );
            return { ...restaurant, logoUrl: imageUrl.data.url };
          })
        );
        setRestaurants(restaurantsWithImages);
      } else {
        throw new Error(
          response.message || "Không thể lấy danh sách nhà hàng đã duyệt"
        );
      }
    } catch (err: any) {
      console.error("Lỗi khi lấy danh sách nhà hàng đã duyệt:", err.message);
      notification.error({
        message: "Lỗi",
        description: err.message,
      });
    }
  };

  useEffect(() => {
    getAllApproved();
  }, []);

  return (
    <div className="container">
      {restaurants.length === 0 ? (
        <p>Không tìm thấy nhà hàng nào đã duyệt.</p>
      ) : (
        <ul className="restaurant-list">
          {restaurants.map((restaurant) => (
            <li key={restaurant.id} className="restaurant-item">
              <a href={`/home/restaurants/${restaurant.id}`}>
                <div className="restaurant-logo">
                  <img
                    src={restaurant.logoUrl}
                    alt={`${restaurant.name} Logo`}
                  />
                </div>
                <div className="restaurant-info">
                  <h3>{restaurant.name}</h3>
                  <p>{restaurant.address}</p>
                  <p>{restaurant.description}</p>
                  <p className="rating">Rating: {restaurant.rating}</p>
                </div>
              </a>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default ApprovedRestaurants;
