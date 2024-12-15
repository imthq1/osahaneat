import React, { useState, useEffect } from "react";
import { Modal, Input, notification } from "antd";
import { IUsers } from "./admin.user.getAll";

interface IProps {
  access_token: string;
  fetchData: () => void;
  isCreateModalOpen: boolean;
  setIsCreateModalOpen: (value: boolean) => void;
  selectedUser: IUsers | null;
}

const UpdateUserModal = (props: IProps) => {
  const {
    access_token,
    fetchData,
    isCreateModalOpen,
    setIsCreateModalOpen,
    selectedUser,
  } = props;

  const [id, setId] = useState(0);
  const [fullname, setFullname] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [address, setAddress] = useState("");
  const [enable, setEnable] = useState("");
  const [role, setRole] = useState("");

  useEffect(() => {
    if (selectedUser) {
      console.log(selectedUser.role);
      setFullname(selectedUser.fullname);
      setEmail(selectedUser.email);
      setId(selectedUser.id);
      setEnable(selectedUser.enable || "DISABLED");
      setAddress(selectedUser.address);
      setRole(selectedUser.role?.roleName || "USER");
    }
  }, [selectedUser]);

  const handleOk = async () => {
    const updatedUser = {
      id,
      fullname,
      email,
      password,
      address,
      enable,
      role: { roleName: role },
    };

    try {
      const response = await fetch(`http://localhost:8080/api/v1/users`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${access_token}`,
        },
        body: JSON.stringify(updatedUser),
      });

      const result = await response.json();

      if (response.ok) {
        notification.success({
          message: "User Updated Successfully",
          description: result.message || "The user was updated successfully.",
        });

        setFullname("");
        setEmail("");
        setEnable("");
        setAddress("");
        setRole("");
        fetchData();
        setIsCreateModalOpen(false);
      } else {
        notification.error({
          message: "Error",
          description:
            result.message || "An error occurred while updating the user.",
        });
      }
    } catch (error) {
      notification.error({
        message: "Network Error",
        description: "Unable to update user. Please try again later.",
      });
      console.error("Error updating user:", error);
    }
  };

  return (
    <Modal
      title="Update User"
      open={isCreateModalOpen}
      onOk={handleOk}
      onCancel={() => setIsCreateModalOpen(false)}
      maskClosable={false}
    >
      <Input
        placeholder="Name"
        value={fullname}
        onChange={(e) => setFullname(e.target.value)}
        style={{ marginBottom: "8px" }}
      />
      <Input
        placeholder="Email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        style={{ marginBottom: "8px" }}
      />

      <Input
        placeholder="Enable"
        value={enable}
        onChange={(e) => setEnable(e.target.value)}
        style={{ marginBottom: "8px" }}
      />
      <Input
        placeholder="Address"
        value={address}
        onChange={(e) => setAddress(e.target.value)}
        style={{ marginBottom: "8px" }}
      />
      <Input
        placeholder="Role (ID)"
        value={role}
        onChange={(e) => setRole(e.target.value)}
        style={{ marginBottom: "8px" }}
      />
    </Modal>
  );
};

export default UpdateUserModal;
