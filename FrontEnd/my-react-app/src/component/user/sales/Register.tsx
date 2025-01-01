import React from "react";
import { FireOutlined } from "@ant-design/icons";
import "../../../style/Home.scss";

const Home = () => {
  const categories = [
    { icon: <FireOutlined />, title: "Popular", options: "286+ options" },
    {
      icon: <FireOutlined />,
      title: "Fast Delivery",
      options: "1,843+ options",
    },
    { icon: <FireOutlined />, title: "High class", options: "25+ options" },
    { icon: <FireOutlined />, title: "Dine in", options: "182+ options" },
    { icon: <FireOutlined />, title: "Pick up", options: "3,548+ options" },
    { icon: <FireOutlined />, title: "Nearest", options: "44+ options" },
  ];

  return (
    <div className="home-container">
      <div className="categories-header">
        <h3>Explore categories</h3>
        <a href="/all-categories" className="see-all">
          See all
        </a>
      </div>
      <div className="categories-grid">
        {categories.map((category, index) => (
          <div key={index} className="category-card">
            <div className="icon-wrapper">{category.icon}</div>
            <div className="category-title">{category.title}</div>
            <div className="category-options">{category.options}</div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Home;
