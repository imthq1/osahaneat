import { useEffect, useState } from "react";
import { restaurantAPI } from "../../api/api.restaurant";
import { notification, Table } from "antd";
import { IRestaurant } from "../../type/restaurant.type";
import "../../style/AdminSeller.scss";
import { EyeOutlined } from "@ant-design/icons";

type NotificationType = "success" | "error";

const AdminSeller = () => {
  const [restaurants, setRestaurants] = useState<IRestaurant[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [api, contextHolder] = notification.useNotification();

  const fetchRestaurants = async () => {
    try {
      const response = await restaurantAPI.getAll();

      if (response.statusCode !== 200) {
        throw new Error(response.message || "Failed to fetch restaurants");
      }

      setRestaurants(response.data.result.content);
    } catch (err) {
      console.error("Error fetching restaurants:", err);
      openNotificationWithIcon(
        "error",
        "Error",
        "Failed to fetch restaurant list."
      );
    } finally {
      setLoading(false);
    }
  };

  const openNotificationWithIcon = (
    type: NotificationType,
    message: string,
    description: string
  ) => {
    api[type]({
      message: message,
      description: description,
    });
  };

  const handleAccept = async (id: number) => {
    try {
      console.log(id);
      const response = await restaurantAPI.acceptRestaurant(id);
      if (response.statusCode === 200) {
        fetchRestaurants;
      } else {
        console.error("Failed to accept restaurant");
      }
    } catch (err) {
      console.error("Error approving restaurant:", err);
    }
  };

  const handleDelete = async (id: number) => {
    try {
      const response = await restaurantAPI.deleteRestaurant(id);
      if (response.statusCode === 200) {
        openNotificationWithIcon(
          "success",
          response.message,
          "The restaurant has been deleted."
        );
        setRestaurants((prev) =>
          prev.filter((restaurant) => restaurant.id !== id)
        );
      } else {
        openNotificationWithIcon(
          "error",
          response.message,
          "Failed to delete the restaurant."
        );
      }
    } catch (err) {
      console.error("Error deleting restaurant:", err);
      openNotificationWithIcon("error", "Error", "An error occurred.");
    }
  };
  const getInfo = async (id: number) => {};
  useEffect(() => {
    fetchRestaurants();
  }, []);

  const columns = [
    {
      title: "Id",
      dataIndex: "id",
      key: "id",
    },
    {
      title: "Name",
      dataIndex: "name",
      key: "name",
    },
    {
      title: "Address",
      dataIndex: "address",
      key: "address",
    },
    {
      title: "Description",
      dataIndex: "description",
      key: "description",
    },

    {
      title: "Status",
      dataIndex: "status",
      key: "status",
      render: (status: string) => (
        <span className="status" data-status={status}>
          {status}
        </span>
      ),
    },
    {
      title: "Action",
      key: "action",
      render: (_: any, record: IRestaurant) => (
        <div className="action-buttons">
          <button className="accept" onClick={() => handleAccept(record.id)}>
            Accept
          </button>
          <button className="delete" onClick={() => handleDelete(record.id)}>
            Delete
          </button>
          <button className="info" onClick={() => getInfo(record.id)}>
            <EyeOutlined />
          </button>
        </div>
      ),
    },
  ];

  return (
    <div className="admin-seller-container">
      {contextHolder}
      <h1>Danh Sách Nhà Hàng</h1>
      <Table
        dataSource={restaurants.map((restaurant) => ({
          ...restaurant,
          key: restaurant.id,
        }))}
        columns={columns}
        loading={loading}
        pagination={{
          pageSize: 10,
        }}
      />
    </div>
  );
};

export default AdminSeller;
