const BASE_URL = "http://localhost:8080/api/v1/food";

class FoodAPI {
  get token() {
    return localStorage.getItem("access_token");
  }

  get headers() {
    return {
      Authorization: `Bearer ${this.token}`,
      "Content-Type": "application/json",
    };
  }

  async getFoodBySeller(page = 1, size = 10) {
    const response = await fetch(
      `${BASE_URL}/getFoodBySeller?page=${page}&size=${size}`,
      {
        method: "GET",
        headers: this.headers,
      }
    );

    if (!response.ok) {
      throw new Error("Không thể lấy danh sách món ăn của seller");
    }

    return response.json();
  }

  async addFood(food: any) {
    const response = await fetch(`${BASE_URL}`, {
      method: "POST",
      headers: this.headers,
      body: JSON.stringify(food),
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || "Thêm món ăn thất bại");
    }

    return response.json();
  }

  async updateFood(id: number, food: any) {
    const response = await fetch(`${BASE_URL}/${id}`, {
      method: "PUT",
      headers: this.headers,
      body: JSON.stringify(food),
    });

    if (!response.ok) {
      throw new Error("Cập nhật món ăn thất bại");
    }

    return response.json();
  }

  async deleteFood(id: number) {
    const response = await fetch(`${BASE_URL}/${id}`, {
      method: "DELETE",
      headers: this.headers,
    });

    if (!response.ok) {
      throw new Error("Xóa món ăn thất bại");
    }

    return response;
  }
}

export const foodAPI = new FoodAPI();
