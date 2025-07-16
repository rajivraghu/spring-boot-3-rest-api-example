package com.bezkoder.spring.restapi.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bezkoder.spring.restapi.model.Product;

@Service
public class ProductService {

  static List<Product> products = new ArrayList<Product>();
  static long id = 0;

  static {
    // Initialize with some default products
    products.add(createProduct("Apple", 1.5));
    products.add(createProduct("Banana", 0.75));
    products.add(createProduct("Orange", 1.25));
  }

  private static Product createProduct(String name, double price) {
    Product product = new Product(name, price);
    product.setId(++id);
    return product;
  }

  public List<Product> findAll() {
    return products;
  }

  public Product findById(long id) {
    return products.stream().filter(product -> id == product.getId()).findAny().orElse(null);
  }

  public Product save(Product product) {
    // update Product
    if (product.getId() != 0) {
      long _id = product.getId();

      for (int idx = 0; idx < products.size(); idx++)
        if (_id == products.get(idx).getId()) {
          products.set(idx, product);
          break;
        }

      return product;
    }

    // create new Product
    product.setId(++id);
    products.add(product);
    return product;
  }

  public void deleteById(long id) {
    products.removeIf(product -> id == product.getId());
  }

  public void deleteAll() {
    products.removeAll(products);
  }
}
