import { IUser } from "../type/user.type";

const BASE_URL = "http://localhost:8080/api/v1";

class UserApi {
  private getHeaders(token: string) {
    return {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    };
  }

  async getAllUsers(token: string) {
    const response = await fetch(`${BASE_URL}/users`, {
      headers: this.getHeaders(token),
    });
    const data = await response.json();
    return data.data.result.content;
  }

  async createUser(token: string, userData: Partial<IUser>) {
    const response = await fetch(`${BASE_URL}/users`, {
      method: "POST",
      headers: this.getHeaders(token),
      body: JSON.stringify(userData),
    });
    return response.ok;
  }

  async updateUser(token: string, userData: Partial<IUser>) {
    const response = await fetch(`${BASE_URL}/users`, {
      method: "PUT",
      headers: this.getHeaders(token),
      body: JSON.stringify(userData),
    });
    return response.ok;
  }

  async deleteUser(token: string, id: number) {
    const response = await fetch(`${BASE_URL}/users/${id}`, {
      method: "DELETE",
      headers: this.getHeaders(token),
    });
    return response.ok;
  }
  async getAccount(token: any) {
    const response = await fetch(`${BASE_URL}/auth/account`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    });
    const result = await response.json();
    return result;
  }
}

export const userApi = new UserApi();
