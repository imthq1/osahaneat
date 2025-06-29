const BASE_URL = "http://localhost:8080/api/v1/categories";
const token = localStorage.getItem("access_token");

class CategoryAPI {
  async getAll() {
    const res = await fetch(BASE_URL, {
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
    });

    if (!res.ok) throw new Error("Không thể lấy danh sách category");
    return res.json();
  }
}

export const categoryAPI = new CategoryAPI();
