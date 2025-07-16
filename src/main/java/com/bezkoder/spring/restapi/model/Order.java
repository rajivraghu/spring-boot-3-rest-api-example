package com.bezkoder.spring.restapi.model;

import java.util.Date;
import java.util.List;

public class Order {
  private long id;
  private List<Product> products;
  private Date orderDate;

  public Order() {
    this.orderDate = new Date();
  }

  public Order(List<Product> products) {
    this.products = products;
    this.orderDate = new Date();
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public List<Product> getProducts() {
    return products;
  }

  public void setProducts(List<Product> products) {
    this.products = products;
  }

  public Date getOrderDate() {
    return orderDate;
  }

  public void setOrderDate(Date orderDate) {
    this.orderDate = orderDate;
  }

  @Override
  public String toString() {
    return "Order [id=" + id + ", products=" + products + ", orderDate=" + orderDate + "]";
  }
}
