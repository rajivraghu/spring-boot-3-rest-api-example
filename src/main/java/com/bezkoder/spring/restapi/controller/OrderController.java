package com.bezkoder.spring.restapi.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bezkoder.spring.restapi.model.Order;
import com.bezkoder.spring.restapi.model.Product;
import com.bezkoder.spring.restapi.service.OrderService;
import com.bezkoder.spring.restapi.service.ProductService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class OrderController {

  @Autowired
  OrderService orderService;

  @Autowired
  ProductService productService;

  @GetMapping("/orders")
  public ResponseEntity<List<Order>> getAllOrders() {
    try {
      List<Order> orders = orderService.findAll();

      if (orders.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }

      return new ResponseEntity<>(orders, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/orders/{id}")
  public ResponseEntity<Order> getOrderById(@PathVariable("id") long id) {
    Order order = orderService.findById(id);

    if (order != null) {
      return new ResponseEntity<>(order, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @PostMapping("/orders")
  public ResponseEntity<Order> createOrder(@RequestBody List<Long> productIds) {
    try {
      List<Product> products = productIds.stream()
          .map(id -> productService.findById(id))
          .filter(product -> product != null)
          .collect(Collectors.toList());

      if (products.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }

      Order _order = orderService.save(new Order(products));
      return new ResponseEntity<>(_order, HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
