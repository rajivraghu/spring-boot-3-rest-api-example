package com.bezkoder.spring.restapi.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bezkoder.spring.restapi.model.Order;

@Service
public class OrderService {

  static List<Order> orders = new ArrayList<Order>();
  static long id = 0;

  public List<Order> findAll() {
    return orders;
  }

  public Order findById(long id) {
    return orders.stream().filter(order -> id == order.getId()).findAny().orElse(null);
  }

  public Order save(Order order) {
    // update Order
    if (order.getId() != 0) {
      long _id = order.getId();

      for (int idx = 0; idx < orders.size(); idx++)
        if (_id == orders.get(idx).getId()) {
          orders.set(idx, order);
          break;
        }

      return order;
    }

    // create new Order
    order.setId(++id);
    orders.add(order);
    return order;
  }

  public void deleteById(long id) {
    orders.removeIf(order -> id == order.getId());
  }

  public void deleteAll() {
    orders.removeAll(orders);
  }
}
