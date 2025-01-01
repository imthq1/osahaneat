const BASE_URL = "http://localhost:8080/api/v1";

const token = localStorage.getItem("access_token");

class RestaurantAPI {
  async getAll() {
    const response = await fetch(`${BASE_URL}/restaurants`, {
      method: "GET",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
    });
    const data = await response.json();
    return data;
  }
  async acceptRestaurant(id: number) {
    const response = await fetch(`${BASE_URL}/restaurants/accept/${id}`, {
      method: "PUT",
      headers: {
        Authorization: `Bearer ${localStorage.getItem("access_token")}`,
        "Content-Type": "application/json",
      },
    });
    return response.json();
  }

  async deleteRestaurant(id: number) {
    const response = await fetch(`${BASE_URL}/restaurants/${id}`, {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${localStorage.getItem("access_token")}`,
        "Content-Type": "application/json",
      },
    });
    return response.json();
  }
  async getAllApproved() {
    const response = await fetch(`${BASE_URL}/restaurants/approved`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      throw new Error("Không thể lấy danh sách nhà hàng đã duyệt");
    }

    return response.json();
  }
}

export const restaurantAPI = new RestaurantAPI();
