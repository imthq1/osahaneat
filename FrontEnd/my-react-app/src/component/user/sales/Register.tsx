import React, { useState, ChangeEvent, FormEvent } from "react";
import "../../../style/SellerRegister.scss";
import { useNavigate } from "react-router";

interface RestaurantFormData {
  name: string;
  address: string;
  description: string;
  content: string;
  logo: string;
}

const SellerRegister: React.FC = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState<RestaurantFormData>({
    name: "",
    address: "",
    description: "",
    content: "",
    logo: "",
  });

  const [tempLogoFile, setTempLogoFile] = useState<File | null>(null);
  const [logoPreview, setLogoPreview] = useState<string>("");
  const [message, setMessage] = useState<string>("");
  const [error, setError] = useState<string>("");

  const handleChange = (
    e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleFileChange = (e: ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    setTempLogoFile(file);
    setLogoPreview(URL.createObjectURL(file));
    setMessage("Ảnh đã chọn, sẽ upload khi bạn gửi đăng ký.");
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setMessage("");
    setError("");

    const token = localStorage.getItem("access_token");
    if (!token) {
      setError("Vui lòng đăng nhập trước khi đăng ký.");
      return;
    }

    try {
      // Nếu có ảnh thì upload trước
      if (tempLogoFile) {
        const uploadForm = new FormData();
        uploadForm.append("file", tempLogoFile);
        uploadForm.append("folder", "restaurant-logos");

        const uploadResponse = await fetch(
          "http://localhost:8080/api/v1/upload/image",
          {
            method: "POST",
            headers: {
              Authorization: `Bearer ${token}`,
            },
            body: uploadForm,
          }
        );

        if (!uploadResponse.ok) {
          const data = await uploadResponse.json();
          throw new Error(data.message || "Lỗi upload ảnh.");
        }

        const uploadResult = await uploadResponse.json();
        formData.logo = uploadResult.data.url; // set publicId vào logo
      }

      // Gửi form đăng ký nhà hàng
      const response = await fetch("http://localhost:8080/api/v1/restaurants", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(formData),
      });

      if (!response.ok) {
        const data = await response.json();
        throw new Error(data.message || "Đã xảy ra lỗi.");
      }
      console.log("FormData", formData);
      setMessage("Đăng ký thành công! Vui lòng chờ admin phê duyệt.");
      // navigate("/home");
    } catch (err: unknown) {
      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError("Lỗi không xác định.");
      }
    }
  };

  return (
    <div className="seller-register-container">
      <h2 className="title">Đăng ký trở thành người bán trên Osanhaneat</h2>
      <form className="register-form" onSubmit={handleSubmit}>
        <label htmlFor="name">Tên nhà hàng</label>
        <input
          id="name"
          name="name"
          placeholder="Tên nhà hàng"
          type="text"
          onChange={handleChange}
          required
        />

        <label htmlFor="address">Địa chỉ</label>
        <input
          id="address"
          name="address"
          placeholder="Địa chỉ"
          type="text"
          onChange={handleChange}
          required
        />

        <label htmlFor="description">Mô tả</label>
        <textarea
          id="description"
          name="description"
          placeholder="Mô tả nhà hàng"
          onChange={handleChange}
          required
        />

        <label htmlFor="content">Nội dung chi tiết</label>
        <textarea
          id="content"
          name="content"
          placeholder="Nội dung chi tiết"
          onChange={handleChange}
        />

        <label htmlFor="logo">Logo nhà hàng</label>
        <input
          id="logo"
          type="file"
          accept="image/*"
          onChange={handleFileChange}
        />
        {logoPreview && (
          <img
            src={logoPreview}
            alt="Logo Preview"
            style={{ width: "150px", marginTop: "10px" }}
          />
        )}

        <button type="submit">Gửi đăng ký</button>
      </form>

      {message && <p className="success">{message}</p>}
      {error && <p className="error">{error}</p>}
    </div>
  );
};

export default SellerRegister;
