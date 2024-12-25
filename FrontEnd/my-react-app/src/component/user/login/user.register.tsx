import { Input, Button, Form, message } from "antd";
import { MailOutlined, LockOutlined, UserOutlined } from "@ant-design/icons";
import "../../../style/register.scss";
import { loginAPI } from "../../../api/user.login";

const RegisterComponent = () => {
  const [form] = Form.useForm();

  const handleSignUp = async (values: any) => {
    const { fullname, email, password } = values;
    try {
      const data = await loginAPI.register(fullname, email, password);
      if (data.error) {
        message.error(data.error);
      } else {
        message.success(
          "Registration successful! Please check your email for verify."
        );
        form.resetFields();
      }
    } catch (error) {
      console.error("Error:", error);
      message.error("An error occurred while registering.");
    }
  };

  return (
    <div className="register-container">
      <div className="register-box">
        <h1 className="register-title">Create Account</h1>
        <p className="register-subtitle">Sign up to get started</p>

        <Form
          form={form}
          layout="vertical"
          className="register-form"
          onFinish={handleSignUp}
        >
          <Form.Item
            label="Full Name"
            name="fullname"
            rules={[
              { required: true, message: "Please enter your full name!" },
            ]}
          >
            <Input
              prefix={<UserOutlined />}
              placeholder="Enter your full name"
              className="form-input"
            />
          </Form.Item>

          <Form.Item
            label="Email"
            name="email"
            rules={[
              { required: true, message: "Please enter your email!" },
              { type: "email", message: "Please enter a valid email!" },
            ]}
          >
            <Input
              prefix={<MailOutlined />}
              placeholder="Enter your email"
              className="form-input"
            />
          </Form.Item>

          <Form.Item
            label="Password"
            name="password"
            rules={[
              { required: true, message: "Please enter your password!" },
              { min: 6, message: "Password must be at least 6 characters!" },
            ]}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="Enter your password"
              className="form-input"
            />
          </Form.Item>

          <Form.Item>
            <Button
              type="primary"
              htmlType="submit"
              block
              className="register-button"
            >
              SIGN UP
            </Button>
          </Form.Item>

          <div className="extra-links">
            Already have an account? <a href="/login">Login here</a>
          </div>
        </Form>
      </div>
    </div>
  );
};

export default RegisterComponent;
