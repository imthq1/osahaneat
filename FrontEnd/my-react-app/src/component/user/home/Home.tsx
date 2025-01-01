import React from "react";
import {
  FireOutlined,
  CarOutlined,
  CrownOutlined,
  ForkOutlined,
  HomeOutlined,
  CompassOutlined,
} from "@ant-design/icons";
import "../../../style/Home.scss";
import ApprovedRestaurants from "./Home.Restaurant";

const Home = () => {
  const categories = [
    { icon: <FireOutlined />, title: "Popular", options: "286+ options" },
    {
      icon: <CarOutlined />,
      title: "Fast Delivery",
      options: "1,843+ options",
    },
    { icon: <CrownOutlined />, title: "High class", options: "25+ options" },
    { icon: <ForkOutlined />, title: "Dine in", options: "182+ options" },
    { icon: <HomeOutlined />, title: "Pick up", options: "3,548+ options" },
    { icon: <CompassOutlined />, title: "Nearest", options: "44+ options" },
  ];

  return (
    <div className="home-container">
      <div className="categories-header">
        <h3>Explore categories</h3>
        <a href="#" className="see-all">
          See all
        </a>
      </div>
      <div className="categories-grid">
        {categories.map((category, index) => (
          <div className="category-card" key={index}>
            <a href="#">
              {" "}
              <div className="icon-wrapper">{category.icon}</div>
            </a>

            <div className="category-title">{category.title}</div>
            <div className="category-options">{category.options}</div>
          </div>
        ))}
      </div>
      <div className="categories-header">
        <h3>Featured restaurants</h3>
        <a href="#" className="see-all">
          See all
        </a>
      </div>
      <ApprovedRestaurants />
    </div>
  );
};

export default Home;
