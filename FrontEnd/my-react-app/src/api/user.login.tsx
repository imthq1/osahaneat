const BASE_URL = "http://localhost:8080/api/v1";

class LoginAPI {
  async login(username: string, password: string) {
    const response = await fetch(`${BASE_URL}/auth/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        username,
        password,
      }),
    });

    if (!response.ok) {
      throw new Error("Failed to login. Please check your credentials.");
    }

    const data = await response.json();
    return data;
  }
}

export default new LoginAPI();
