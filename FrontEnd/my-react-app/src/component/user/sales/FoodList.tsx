import React, { useEffect, useState } from "react";
import { foodAPI } from "../../../api/seller.foodApi";
import { upload } from "../../../api/api.upload";
import { categoryAPI } from "../../../api/seller.categoryApi";
import "../../../style/seller/FoodList.scss";

type Category = {
  id: number;
  name?: string;
};

type Food = {
  id?: number;
  name: string;
  logo: string;
  price: number;
  quantity: number;
  description: string;
  intruction: string;
  category: Category;
};

type PaginationMeta = {
  page: number;
  pageSize: number;
  pages: number;
  total: number;
};

const FoodList: React.FC = () => {
  const [foods, setFoods] = useState<Food[]>([]);
  const [meta, setMeta] = useState<PaginationMeta | null>(null);
  const [categories, setCategories] = useState<Category[]>([]);
  const [page, setPage] = useState(1);
  const [showForm, setShowForm] = useState(false);
  const pageSize = 5;

  const [newFood, setNewFood] = useState<Food>({
    name: "",
    logo: "",
    price: 0,
    quantity: 0,
    description: "",
    intruction: "",
    category: { id: 0 },
  });

  const [file, setFile] = useState<File | null>(null);
  const [editingId, setEditingId] = useState<number | null>(null);

  const loadFoods = async () => {
    try {
      const data = await foodAPI.getFoodBySeller(page, pageSize);
      setFoods(data.data.result);
      setMeta(data.meta);
    } catch (error) {
      console.error("Lỗi khi tải món ăn:", error);
    }
  };

  const loadCategories = async () => {
    try {
      const res = await categoryAPI.getAll();
      setCategories(res.data);
    } catch (err) {
      console.error("Lỗi khi tải danh mục:", err);
    }
  };

  useEffect(() => {
    loadFoods();
    loadCategories();
  }, [page]);

  const handleUploadImage = async (): Promise<string> => {
    if (!file) return newFood.logo;
    const res = await upload.uploadImage(file, "logo-food");
    return res.data.url;
  };

  const handleAddOrUpdateFood = async () => {
    try {
      const imageUrl = await handleUploadImage();
      const foodData: Food = { ...newFood, logo: imageUrl };

      if (editingId) {
        await foodAPI.updateFood(editingId, foodData);
      } else {
        await foodAPI.addFood(foodData);
      }

      setNewFood({
        name: "",
        logo: "",
        price: 0,
        quantity: 0,
        description: "",
        intruction: "",
        category: { id: 0 },
      });
      setFile(null);
      setEditingId(null);
      setShowForm(false);
      await loadFoods();
    } catch (err: any) {
      console.error("Lỗi khi thêm/sửa món ăn:", err.message);
    }
  };

  const handleEdit = (food: Food) => {
    setNewFood(food);
    setEditingId(food.id ?? null);
    setShowForm(true);
  };

  const handleDelete = async (id: number) => {
    try {
      await foodAPI.deleteFood(id);
      await loadFoods();
    } catch (error) {
      console.error("Lỗi khi xóa:", error);
    }
  };

  return (
    <div className="food-management">
      <button className="add-food-button" onClick={() => setShowForm(true)}>
        Thêm món ăn
      </button>

      {showForm && (
        <>
          <div className="modal-backdrop" onClick={() => setShowForm(false)} />
          <div className="form-section">
            <h2>{editingId ? "Chỉnh sửa món ăn" : "Thêm món ăn"}</h2>
            <label htmlFor="name">Tên món ăn</label>
            <input
              id="name"
              type="text"
              value={newFood.name}
              onChange={(e) => setNewFood({ ...newFood, name: e.target.value })}
            />
            <label htmlFor="price">Giá</label>
            <input
              id="price"
              type="number"
              value={newFood.price}
              onChange={(e) =>
                setNewFood({ ...newFood, price: +e.target.value })
              }
            />
            <label htmlFor="quantity">Số lượng</label>
            <input
              id="quantity"
              type="number"
              value={newFood.quantity}
              onChange={(e) =>
                setNewFood({ ...newFood, quantity: +e.target.value })
              }
            />
            <label htmlFor="description">Mô tả</label>
            <textarea
              id="description"
              value={newFood.description}
              onChange={(e) =>
                setNewFood({ ...newFood, description: e.target.value })
              }
            />
            <label htmlFor="intruction">Hướng dẫn</label>
            <textarea
              id="intruction"
              value={newFood.intruction}
              onChange={(e) =>
                setNewFood({ ...newFood, intruction: e.target.value })
              }
            />
            <label htmlFor="category">Danh mục</label>
            <select
              id="category"
              value={newFood.category?.id || 0}
              onChange={(e) =>
                setNewFood({
                  ...newFood,
                  category: { id: +e.target.value },
                })
              }
            >
              <option value={0} disabled>
                -- Chọn danh mục --
              </option>
              {categories.map((cat) => (
                <option key={cat.id} value={cat.id}>
                  {cat.name}
                </option>
              ))}
            </select>
            <label htmlFor="image-upload">Ảnh món ăn</label>
            <div className="file-upload">
              <input
                id="image-upload"
                type="file"
                accept="image/*"
                onChange={(e) => setFile(e.target.files?.[0] ?? null)}
              />
              {file && (
                <img
                  src={URL.createObjectURL(file)}
                  alt="Ảnh xem trước"
                  className="preview-image"
                />
              )}
            </div>
            <div className="form-actions">
              <button className="create-button" onClick={handleAddOrUpdateFood}>
                {editingId ? "Cập nhật" : "Tạo món"}
              </button>
              <button
                className="cancel-button"
                onClick={() => {
                  setShowForm(false);
                  setFile(null);
                  setEditingId(null);
                }}
              >
                Hủy
              </button>
            </div>
          </div>
        </>
      )}

      <div className="food-grid">
        {foods.map((food) => (
          <div className="food-card" key={food.id}>
            <img
              src={`https://res.cloudinary.com/dw4yj3kvq/image/upload/v1750081399/${food.logo}`}
              alt={food.name}
            />
            <div className="food-info">
              <h2>{food.name}</h2>
              <p>{food.price.toLocaleString()} đ</p>
              <div className="actions">
                <button onClick={() => handleEdit(food)}>✏️</button>
                <button onClick={() => handleDelete(food.id!)}>🗑️</button>
              </div>
            </div>
          </div>
        ))}
      </div>

      {meta && (
        <div className="pagination">
          <button disabled={page <= 1} onClick={() => setPage(page - 1)}>
            ◀
          </button>
          <span>
            Trang {meta.page} / {meta.pages}
          </span>
          <button
            disabled={page >= meta.pages}
            onClick={() => setPage(page + 1)}
          >
            ▶
          </button>
        </div>
      )}
    </div>
  );
};

export default FoodList;
