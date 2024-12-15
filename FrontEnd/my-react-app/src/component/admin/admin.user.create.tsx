import { Input, Modal, notification, Button, Upload } from "antd";
import { useState } from "react";
import { UploadOutlined } from "@ant-design/icons";
import type { UploadProps } from "antd";

interface IProps {
  access_token: string;
  fetchData: () => void;
  isCreateModalOpen: boolean;
  setIsCreateModalOpen: (value: boolean) => void;
}

const CreateUserModal = (props: IProps) => {
  const [fullname, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [age, setAge] = useState("");
  const [gender, setGender] = useState("");
  const [address, setAddress] = useState("");
  const [role, setRole] = useState(2);
  const [imageUrl, setImageUrl] = useState<string | null>(null);

  const { access_token, fetchData, isCreateModalOpen, setIsCreateModalOpen } =
    props;

  const UploadProps: UploadProps = {
    name: "file",
    action: "http://localhost:8080/api/v1/upload/image",
    headers: {
      Authorization: `Bearer ${access_token}`,
    },
    data: {
      folder: "user-images",
    },
    onChange(info) {
      if (info.file.status === "uploading") {
        console.log("Uploading:", info.file.name);
      }
      if (info.file.status === "done") {
        const { data } = info.file.response || {};
        if (data && Array.isArray(data) && data.length > 0) {
          const uploadedFile = data[0];
          const { name, url } = uploadedFile;
          console.log(url);
          setImageUrl(url);
          notification.success({
            message: "File uploaded successfully",
            description: `${name} uploaded successfully!`,
          });
        } else {
          notification.error({
            message: "File upload failed",
            description: "No valid data received from server.",
          });
        }
      } else if (info.file.status === "error") {
        notification.error({
          message: "File upload failed",
          description: `${info.file.name} failed to upload.`,
        });
      }
    },
  };

  const handleOk = async () => {
    if (!fullname || !email || !password || !address) {
      notification.error({
        message: "Missing Fields",
        description: "Please fill in all required fields.",
      });
      return;
    }

    const newUser = {
      fullname,
      email,
      password,
      age,
      gender,
      address,
      role: { id: role },
      imageUrl,
    };

    try {
      const response = await fetch("http://localhost:8080/api/v1/users", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${access_token}`,
        },
        body: JSON.stringify(newUser),
      });

      const result = await response.json();

      if (response.ok) {
        notification.success({
          message: "User Created Successfully",
          description: result.message || "The user was created successfully.",
        });

        setName("");
        setEmail("");
        setPassword("");
        setAge("");
        setGender("");
        setAddress("");
        setRole(2);
        setImageUrl(null);

        fetchData();
        setIsCreateModalOpen(false);
      } else {
        notification.error({
          message: "Error",
          description:
            result.message || "An error occurred while creating the user.",
        });
      }
    } catch (error) {
      notification.error({
        message: "Network Error",
        description: "Unable to create user. Please try again later.",
      });
      console.error("Error creating user:", error);
    }
  };

  return (
    <Modal
      title="Add New User"
      open={isCreateModalOpen}
      onOk={handleOk}
      onCancel={() => setIsCreateModalOpen(false)}
      maskClosable={false}
      okButtonProps={{
        disabled: !fullname || !email || !password || !address,
      }}
    >
      <Input
        placeholder="Name"
        value={fullname}
        onChange={(e) => setName(e.target.value)}
        style={{ marginBottom: "8px" }}
      />
      <Input
        placeholder="Email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        style={{ marginBottom: "8px" }}
      />
      <Input.Password
        placeholder="Password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        style={{ marginBottom: "8px" }}
      />
      <Input
        placeholder="Age"
        value={age}
        onChange={(e) => setAge(e.target.value)}
        style={{ marginBottom: "8px" }}
      />
      <Input
        placeholder="Gender"
        value={gender}
        onChange={(e) => setGender(e.target.value)}
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
        onChange={(e) => setRole(Number(e.target.value))}
        style={{ marginBottom: "8px" }}
      />
      <Upload {...UploadProps}>
        <Button icon={<UploadOutlined />}>Click to Upload</Button>
      </Upload>
    </Modal>
  );
};

export default CreateUserModal;
