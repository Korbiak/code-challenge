package com.korbiak.challenge.service.impl;

import com.korbiak.challenge.dto.request.ProductRequest;
import com.korbiak.challenge.dto.response.ProductResponse;
import com.korbiak.challenge.feign.FixerClient;
import com.korbiak.challenge.feign.FixerResponse;
import com.korbiak.challenge.handler.exception.FixerException;
import com.korbiak.challenge.mapper.ProductMapper;
import com.korbiak.challenge.model.Product;
import com.korbiak.challenge.repository.CategoryRepository;
import com.korbiak.challenge.repository.ProductRepository;
import com.korbiak.challenge.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final FixerClient fixerClient;

    @Override
    public List<ProductResponse> findAllProducts() {
        log.info("Getting list of all products");
        List<Product> products = productRepository.findAll();

        return products.stream()
                .map(productMapper::modelToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse findProductById(int id) {
        log.info("Getting product by id = " + id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product with id= " + id + "not found");
                    return new EntityNotFoundException("Product with id= " + id + "not found");
                });

        return productMapper.modelToResponse(product);
    }

    @Override
    public ProductResponse saveProduct(ProductRequest productRequest, String coin) {
        log.info("Saving product = " + productRequest + " with coin = " + coin);
        checkValidCategory(productRequest.getCategory().getId());

        Product product = productMapper.requestToModel(productRequest);
        product.setPrice(product.getPrice() / getRateOfGivenCurrency(coin));
        productRepository.save(product);

        return productMapper.modelToResponse(product);
    }

    @Override
    public ProductResponse updateProductById(int id, ProductRequest productRequest, String coin) {
        log.info("Updating product with id = " + id + " to product = " + productRequest);
        checkValidCategory(productRequest.getCategory().getId());
        if (!productRepository.existsById(id)) {
            log.error("Product with id= " + id + "not found");
            throw new EntityNotFoundException("Product with id= " + id + "not found");
        }

        Product product = productMapper.requestToModel(productRequest);
        product.setPrice(product.getPrice() / getRateOfGivenCurrency(coin));
        product.setId(id);
        productRepository.save(product);

        return productMapper.modelToResponse(product);
    }

    @Override
    public void deleteProductById(int id) {
        log.info("Deleting product with id = " + id);
        productRepository.deleteById(id);
    }

    private void checkValidCategory(int id) {
        log.info("Checking category with id = " + id);
        if (!categoryRepository.existsById(id)) {
            log.error("Category with id = " + id + " not found");
            throw new EntityNotFoundException("Category with id = " + id + " not found");
        }
    }

    private double getRateOfGivenCurrency(String coin) {
        log.info("Getting rate of " + coin + " currency");
        FixerResponse fixerResponse = fixerClient.getRecentExchangeRateData();
        if (fixerResponse.isSuccess()) {
            Map<String, Double> rates = fixerResponse.getRates();
            if (rates.containsKey(coin.toUpperCase())) {
                return rates.get(coin);
            } else {
                log.error(coin + " currency not found");
                throw new IllegalArgumentException(coin + " currency not found");
            }
        }
        log.error("Fixer.io return incorrect response");
        throw new FixerException("Fixer error");
    }
}
