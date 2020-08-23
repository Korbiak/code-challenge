package com.korbiak.challenge.service;

import com.korbiak.challenge.dto.request.ProductRequest;
import com.korbiak.challenge.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {

    List<ProductResponse> findAllProducts();

    ProductResponse findProductById(int id);

    ProductResponse saveProduct(ProductRequest productRequest, String coin);

    ProductResponse updateProductById(int id, ProductRequest productRequest, String coin);

    void deleteProductById(int id);
}
