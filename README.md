
# OsahaNEat

**OsahaNEat** is a full-stack online food ordering application built with React for the frontend and Spring Boot for the backend.  
It allows customers to browse menus, place orders, and track delivery, while providing administrators with tools to manage restaurants, menus, and orders.

##  Key Features

- User Registration & Authentication: Secure sign-up and login using OAuth2 (Google Sign-In) or email/password. Authentication is managed using JWT to securely maintain user sessions.
- **Restaurant & Menu Management**: Admins can create, update, and delete restaurants and menu items.
- **Shopping Cart & Checkout**: Customers can add items to a cart, apply VNPAY for payments, and view order summaries.
- **Order Processing**: Real-time order queue with RabbitMQ for efficient message handling.
- **Image Upload**: Cloudinary integration for storing restaurant and dish photos.
- **Email Notifications**: Order confirmations and status updates sent via email.

##  Technology Stack

### Backend (Spring Boot)

- **Framework**: Spring Boot
- **Database**: MySQL
- **Cloud Storage**: Cloudinary
- **Payment Gateway**: VNPAY
- **Messaging**: RabbitMQ
- **Security**: Spring Security with OAuth2 (Google) + JWT
- **Email Service**: JavaMailSender

### Frontend (React + TypeScript)

- **Framework**: React
- **Language**: TypeScript
- **UI Library**: Ant Design
- **Styling**: SCSS

##  Project Structure

```
osahaneat/
│
├── FrontEnd/                 # React application
│   └── my-react-app/         # Source code for the web client
│
├── demo/                     # Spring Boot backend
│   └── src/                  # Java source files and resources
│
├── .idea/                    # IDE configuration files
└── README.md                 # Project documentation
```

##  Setup & Installation

### Backend

Clone the repository and navigate to the `demo` folder:

```bash
git clone https://github.com/<your-username>/osahaneat.git
cd osahaneat/demo
```

Configure database and external services in `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/osahaneat
    username: root
    password: your_password

cloudinary:
  cloud-name: your_cloud_name
  api-key: your_api_key
  api-secret: your_api_secret

vnpay:
  tmn-code: your_tmn_code
  hash-secret: your_hash_secret

rabbitmq:
  host: localhost
  port: 5672
```

Start RabbitMQ via Docker:

```bash
docker run -d --hostname rabbitmq --name rabbitmq -p 5672:5672 rabbitmq:3-management
```

Build and run the Spring Boot application:

```bash
mvn clean install
mvn spring-boot:run
```

### Frontend

Navigate to the React app folder:

```bash
cd ../FrontEnd/my-react-app
```

Install dependencies and start the development server:

```bash
npm install
npm start
```

Open [http://localhost:3000](http://localhost:3000) in your browser.

---

## Login Form
![image](https://github.com/user-attachments/assets/3cf71505-eee7-493b-a735-030669641d76)


![image](https://github.com/user-attachments/assets/db74d2fd-6078-4d13-8d7d-dcafb584692b)


![image](https://github.com/user-attachments/assets/05e2816c-ab26-41e9-90b6-c7454370a9b4)

![image](https://github.com/user-attachments/assets/f01896bf-be79-4e98-b8e6-72e0b9d84bb6)


## Home Page
![image](https://github.com/user-attachments/assets/3c2ffd84-3859-4695-b091-589b6a622f15)



