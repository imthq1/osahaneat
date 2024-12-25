import { Button, Pagination, Table, notification } from "antd";
import React, { useEffect, useState } from "react";
import "../../style/tableUser.scss";
import { PlusOutlined, EditOutlined, DeleteOutlined } from "@ant-design/icons";
import CreateUserModal from "./admin.user.create";
import UpdateUserModal from "./admin.user.update";
import { userApi } from "../../api/user.api";

interface IRole {
  id: number;
  roleName: string;
}

export interface IUsers {
  id: number;
  fullname: string;
  email: string;
  address: string;
  enable: string | null;
  role: IRole;
}

const GetAllUser: React.FC = () => {
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [isUpdateModalOpen, setIsUpdateModalOpen] = useState(false);
  const [selectedUser, setSelectedUser] = useState<IUsers | null>(null);
  const [listUsers, setListUsers] = useState<IUsers[]>([]);
  const token = localStorage.getItem("access_token") as string;

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const response = await userApi.getAllUsers(token);
      setListUsers(response);
    } catch (error) {
      console.error("Error fetching users:", error);
    }
  };

  const handleEdit = (user: IUsers) => {
    setSelectedUser(user);
    setIsUpdateModalOpen(true);
  };

  const handleDelete = async (id: number) => {
    try {
      const response = await fetch(`http://localhost:8080/api/v1/users/${id}`, {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });

      if (response.ok) {
        notification.success({
          message: "User Deleted",
          description: "The user was successfully deleted.",
        });
        fetchData();
      } else {
        notification.error({
          message: "Error Deleting User",
          description: "Failed to delete the user.",
        });
      }
    } catch (error) {
      notification.error({
        message: "Network Error",
        description: "An error occurred while deleting the user.",
      });
      console.error("Error deleting user:", error);
    }
  };

  const columns = [
    {
      title: "Id",
      dataIndex: "id",
      key: "id",
    },
    {
      title: "Name",
      dataIndex: "fullname",
      key: "name",
    },
    {
      title: "Email",
      dataIndex: "email",
      key: "email",
    },
    {
      title: "Address",
      dataIndex: "address",
      key: "address",
    },
    {
      title: "Enable",
      dataIndex: "enable",
      key: "enable",
    },
    {
      title: "Actions",
      key: "actions",
      render: (record: IUsers) => (
        <div>
          <Button
            icon={<EditOutlined />}
            onClick={() => handleEdit(record)}
            style={{ marginRight: 8 }}
          >
            Edit
          </Button>
          <Button
            icon={<DeleteOutlined />}
            onClick={() => handleDelete(record.id)}
            danger
          >
            Delete
          </Button>
        </div>
      ),
    },
  ];

  return (
    <>
      <div className="Table-users">
        <Button
          icon={<PlusOutlined />}
          type="primary"
          onClick={() => setIsCreateModalOpen(true)}
          className="add-user-button"
        >
          Add User
        </Button>
      </div>
      <CreateUserModal
        access_token={token}
        fetchData={fetchData}
        isCreateModalOpen={isCreateModalOpen}
        setIsCreateModalOpen={setIsCreateModalOpen}
      />

      <UpdateUserModal
        access_token={token}
        fetchData={fetchData}
        isCreateModalOpen={isUpdateModalOpen}
        setIsCreateModalOpen={setIsUpdateModalOpen}
        selectedUser={selectedUser}
      />

      <Table
        className="user-table"
        dataSource={listUsers}
        columns={columns}
        rowKey="id"
      >
        <Pagination defaultPageSize={1}></Pagination>
      </Table>
    </>
  );
};

export default GetAllUser;
