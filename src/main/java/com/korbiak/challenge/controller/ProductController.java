package com.korbiak.challenge.controller;

import com.korbiak.challenge.dto.request.ProductRequest;
import com.korbiak.challenge.dto.response.ProductResponse;
import com.korbiak.challenge.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("products/")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<ProductResponse> findAllProducts() {
        return productService.findAllProducts();
    }

    @GetMapping("/{id}")
    public ProductResponse findProductById(@PathVariable int id) {
        return productService.findProductById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse saveNewProduct(@RequestBody @Validated ProductRequest productRequest,
                                          @RequestParam String coin) {
        return productService.saveProduct(productRequest, coin);
    }

    @PutMapping("/{id}")
    public ProductResponse updateProductById(@PathVariable int id, @RequestBody @Validated ProductRequest productRequest,
                                             @RequestParam String coin) {
        return productService.updateProductById(id, productRequest, coin);
    }

    @DeleteMapping("/{id}")
    public void deleteProductById(@PathVariable int id) {
        productService.deleteProductById(id);
    }
}
