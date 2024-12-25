import { Input, Modal, notification, Button, Upload } from "antd";
import { useState } from "react";
import { UploadOutlined } from "@ant-design/icons";
import type { UploadProps } from "antd";
import { upload } from "../../api/api.upload";

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
  const [image, setImage] = useState<File | null>(null);
  const [image_url, setImageUrl] = useState<string | null>(null);
  const { access_token, fetchData, isCreateModalOpen, setIsCreateModalOpen } =
    props;

  const uploadProps: UploadProps = {
    maxCount: 1,
    beforeUpload: (file) => {
      setImage(file);
      notification.success({
        message: "File Ready for Upload",
        description: `${file.name} has been selected.`,
      });
      return false;
    },
    onRemove: () => {
      setImage(null);
      notification.info({
        message: "File Removed",
      });
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

    if (image) {
      try {
        const response = await upload.uploadImage(image, "user-images");
        setImageUrl(response.data.url);
      } catch (error) {
        notification.error({
          message: "Image Upload Failed",
          description: "Unable to upload the image. Please try again.",
        });
        return;
      }
    }

    const newUser = {
      fullname,
      email,
      password,
      age,
      gender,
      address,
      image_url,
      role: { id: role },
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
        setImage(null);

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
      <Upload {...uploadProps}>
        <Button icon={<UploadOutlined />}>Click to Upload</Button>
      </Upload>
    </Modal>
  );
};

export default CreateUserModal;
