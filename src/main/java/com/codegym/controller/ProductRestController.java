package com.codegym.controller;

import com.codegym.model.Product;
import com.codegym.service.product.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/products")
public class ProductRestController {
    @Autowired
    private IProductService productService;
    @GetMapping
    public ResponseEntity<Iterable<Product>>findAll(@RequestParam(name = "q")Optional<String>q){
        Iterable<Product> products = productService.findAll();
        if (q.isPresent()){
            products = productService.findAllByNameContaining(q.get());
        }
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
}
