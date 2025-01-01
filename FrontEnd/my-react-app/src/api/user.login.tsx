const BASE_URL = "http://localhost:8080/api/v1";
const token = localStorage.getItem("access_token");
const GOOGLE_AUTH_URL = `https://accounts.google.com/o/oauth2/v2/auth/oauthchooseaccount?response_type=code&client_id=876120705043-46ochn7908876r8ncdrokg4ka133r5bh.apps.googleusercontent.com&scope=profile%20email&state=Pyn08lGLIKAGNsFecS1c888kA1sAOiFp3tXw85PuKcY%3D&redirect_uri=http%3A%2F%2Flocalhost%3A5173%2Flogin%2Fgoogle&service=lso&o2v=2&ddm=1&flowName=GeneralOAuthFlow`;
class LoginAPI {
  async loginGoogle() {
    window.location.href = GOOGLE_AUTH_URL;
  }
  // Login API
  async logout(navigate: any) {
    try {
      const response = await fetch(`${BASE_URL}/auth/logout`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        credentials: "include",
      });

      if (response.ok) {
        localStorage.removeItem("access_token");
        navigate("/login");
      } else {
        console.error("Logout failed");
      }
    } catch (error) {
      console.error("Error during logout:", error);
    }
  }

  async login(username: string, password: string) {
    const response = await fetch(`${BASE_URL}/auth/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ username, password }),
      credentials: "include",
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(
        errorData.message || "Failed to login. Please check your credentials."
      );
    }

    return await response.json();
  }

  // Forgot Password API
  async forgot(email: string) {
    const response = await fetch(`${BASE_URL}/forget/pass?email=${email}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || "Failed to send reset email.");
    }

    return await response.json();
  }

  // Register API
  async register(fullname: string, email: string, password: string) {
    const response = await fetch(`${BASE_URL}/auth/register`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ fullname, email, password }),
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || "Registration failed.");
    }

    return await response.json();
  }

  async verifyEmail(token: string): Promise<void> {
    const response = await fetch(`${BASE_URL}/auth/verify?token=${token}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || "Email verification failed.");
    }

    return await response.json();
  }
}

export const loginAPI = new LoginAPI();
