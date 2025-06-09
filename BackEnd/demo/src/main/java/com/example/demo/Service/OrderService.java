package com.example.demo.Service;

import com.example.demo.Domain.Food;
import com.example.demo.Domain.Order;
import com.example.demo.Domain.User;
import com.example.demo.Domain.request.OrderRequest;
import com.example.demo.Domain.response.OrderDTO;
import com.example.demo.Repository.FoodRepository;
import com.example.demo.Repository.OderRepository;
import com.example.demo.util.SecurityUtil;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OderRepository oderRepository;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    private final FoodRepository foodRepository;
    public OrderService(OderRepository oderRepository, SecurityUtil securityUtil, UserService userService, FoodRepository foodRepository) {
        this.oderRepository = oderRepository;
        this.securityUtil = securityUtil;
        this.userService = userService;
        this.foodRepository = foodRepository;
    }
    public Order save(Order order)
    {
        return this.oderRepository.save(order);
    }
    public Order findById(long id) {
        return this.oderRepository.findById(id);
    }
    public OrderDTO OrderToDTO(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        List<OrderDTO.Food> orderDTOFood = new ArrayList<>();

        orderDTO.setId(order.getId());
        orderDTO.setStatus(order.getStatus());
        orderDTO.setCreateDate(order.getCreateDate());
        orderDTO.setTotalPrice(order.getTotalPrice());

        if (order.getFoods() != null) {
            List<Long> listId = order.getFoods().stream()
                    .map(Food::getId)
                    .collect(Collectors.toList());

            List<Food> foods = foodRepository.findByIdIn(listId);

            for (Food food : foods) {
                OrderDTO.Food dtoFood = new OrderDTO.Food();
                dtoFood.setName(food.getName());
                dtoFood.setPrice(food.getPrice());
                dtoFood.setQuantity(food.getQuantity());

                orderDTOFood.add(dtoFood);
            }
            orderDTO.setFoodList(orderDTOFood);
        }

        return orderDTO;
    }
    public Boolean validateFood(OrderRequest orderRequest){

        if (orderRequest.getFoods() != null) {
            List<Long> listFoodIds = orderRequest.getFoods().stream()
                    .map(OrderRequest.FoodOrder::getFoodId)
                    .collect(Collectors.toList());
            List<Food> foods = this.foodRepository.findByIdIn(listFoodIds);

            Map<Long, Food> foodMap = new HashMap<>();
            for (Food food : foods) {
                foodMap.put(food.getId(), food);
            }
            for (OrderRequest.FoodOrder foodOrder : orderRequest.getFoods()) {
                Food food = foodMap.get(foodOrder.getFoodId());
                if (food.getQuantity() < foodOrder.getQuantity()) {
                    return false;
                }
            }
        }
        return true;
    }
    @Transactional
    public synchronized Order processOrder(OrderRequest request) {
        if (!validateFood(request)) {
            throw new IllegalStateException("Not enough quantity");
        }
        return createOrder(request);
    }
    public Order createOrder(OrderRequest orderRequest) {
        String name=this.securityUtil.getCurrentUserLogin().get();
        User user=this.userService.findByEmail(name);

        Order order = new Order();
        order.setUsers(user);


        if (orderRequest.getFoods() != null) {
            List<Long> listFoodIds = orderRequest.getFoods().stream()
                    .map(OrderRequest.FoodOrder::getFoodId)
                    .collect(Collectors.toList());

            List<Food> foods = this.foodRepository.findByIdIn(listFoodIds);

            List<Food> orderedFoods = new ArrayList<>(); 
            Map<Long, Food> foodMap = new HashMap<>();

            for (Food food : foods) {
                foodMap.put(food.getId(), food);
            }
            double totalPrice = 0;

            for (OrderRequest.FoodOrder foodOrder : orderRequest.getFoods()) {
                Food food = foodMap.get(foodOrder.getFoodId());
                if (food == null) {
                    throw new IllegalArgumentException("Món ăn không tồn tại: " + foodOrder.getFoodId());
                }

                if (food.getQuantity() < foodOrder.getQuantity()) {
                    throw new IllegalArgumentException("Không đủ số lượng cho món: " + food.getName());
                }
                totalPrice += foodOrder.getQuantity() * food.getPrice();
                food.setQuantity(food.getQuantity() - foodOrder.getQuantity());
                orderedFoods.add(food);
                this.foodRepository.save(food);
            }
            order.setTotalPrice(totalPrice);
            order.setFoods(orderedFoods);
        }

        return oderRepository.save(order);
    }
}
