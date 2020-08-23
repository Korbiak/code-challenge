package com.korbiak.challenge;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.korbiak.challenge.dto.request.ProductRequest;
import com.korbiak.challenge.dto.response.CategoryResponse;
import com.korbiak.challenge.feign.FixerClient;
import com.korbiak.challenge.feign.FixerResponse;
import com.korbiak.challenge.model.Category;
import com.korbiak.challenge.model.Product;
import com.korbiak.challenge.repository.CategoryRepository;
import com.korbiak.challenge.repository.ProductRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Time;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
public class ProductControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objMapper;

    @MockBean
    private ProductRepository productRepositoryMock;

    @MockBean
    private CategoryRepository categoryRepositoryMock;

    @MockBean
    private FixerClient fixerClientMock;

    private final ProductRequest productRequest =
            new ProductRequest(
                    "name",
                    "description",
                    144,
                    new Date(),
                    new CategoryResponse(
                            1,
                            "name"
                    )
            );

    private final Product product =
            new Product(
                    1,
                    "name",
                    "description",
                    144,
                    new Date(),
                    new Category(
                            1,
                            "name",
                            null
                    )
            );

    private final FixerResponse fixerResponse =
            new FixerResponse(
                    true,
                    new Time(14),
                    "base",
                    new Date(),
                    new HashMap<>()
            );

    @Before
    public void setUpMocks() {
        when(categoryRepositoryMock.existsById(productRequest.getCategory().getId()))
                .thenReturn(true);
    }


    @Test
    public void findAllProductTest() throws Exception {
        when(productRepositoryMock.findAll())
                .thenReturn(Collections.singletonList(product));

        mockMvc.perform(get("/products/")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").isNotEmpty())
                .andExpect(jsonPath("$.[0].name").value(product.getName()))
                .andExpect(jsonPath("$.[0].description").value(product.getDescription()))
                .andExpect(jsonPath("$.[0].price").value(product.getPrice()))
                .andExpect(jsonPath("$.[0].category").isNotEmpty());
    }

    @Test
    public void findProductByIdTest() throws Exception {
        int id = 666;

        when(productRepositoryMock.findById(id))
                .thenAnswer(invocationOnMock -> {
                    Product product1 = product;
                    product1.setId(invocationOnMock.getArgument(0));
                    return Optional.of(product1);
                });

        mockMvc.perform(get("/products/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(product.getName()))
                .andExpect(jsonPath("$.description").value(product.getDescription()))
                .andExpect(jsonPath("$.price").value(product.getPrice()))
                .andExpect(jsonPath("$.category").isNotEmpty());
    }

    @Test
    public void findProductByIncorrectIdTestCase() throws Exception {
        int id = 666;

        when(productRepositoryMock.findById(id))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/products/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Product with id = " + id + " not found"));
    }

    @Test
    public void saveNewProductTest() throws Exception {
        String coin = "UAH";
        FixerResponse fixerResponseTest = fixerResponse;
        HashMap<String, Double> currency = new HashMap<>();
        currency.put(coin, 32.0);
        fixerResponseTest.setRates(currency);

        when(productRepositoryMock.save(Mockito.any(Product.class)))
                .thenAnswer(invocationOnMock -> {
                    Product product = invocationOnMock.getArgument(0);
                    product.setId(45);
                    return product;
                });

        when(fixerClientMock.getRecentExchangeRateData()).thenReturn(fixerResponseTest);

        mockMvc.perform(post("/products/?coin={coin}", coin)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(45))
                .andExpect(jsonPath("$.name").value(productRequest.getName()))
                .andExpect(jsonPath("$.description").value(productRequest.getDescription()))
                .andExpect(jsonPath("$.price").value(productRequest.getPrice() / currency.get(coin)))
                .andExpect(jsonPath("$.category").isNotEmpty());
    }

    @Test
    public void saveNewProductTestIncorrectCategoryIdCase() throws Exception {
        String coin = "UAH";
        when(categoryRepositoryMock.existsById(productRequest.getCategory().getId()))
                .thenReturn(false);

        mockMvc.perform(post("/products/?coin={coin}", coin)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objMapper.writeValueAsString(productRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Category with id = " + productRequest.getCategory().getId() + " not found"));
    }

    @Test
    public void saveNewProductTestIncorrectCoinCase() throws Exception {
        String coin = "UAH";

        when(fixerClientMock.getRecentExchangeRateData()).thenReturn(fixerResponse);

        mockMvc.perform(post("/products/?coin={coin}", coin)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objMapper.writeValueAsString(productRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value(coin + " currency not found"));
    }

    @Test
    public void saveNewProductTestIncorrectFeinResponseCase() throws Exception {
        String coin = "UAH";
        FixerResponse fixerResponse1 = fixerResponse;
        fixerResponse1.setSuccess(false);

        when(fixerClientMock.getRecentExchangeRateData()).thenReturn(fixerResponse1);

        mockMvc.perform(post("/products/?coin={coin}", coin)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objMapper.writeValueAsString(productRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Fixer error"));
    }

    @Test
    public void updateProductByIdTest() throws Exception {
        int id = 20;
        String coin = "UAH";
        FixerResponse fixerResponse1 = fixerResponse;
        HashMap<String, Double> currency = new HashMap<>();
        currency.put(coin, 32.0);
        fixerResponse1.setRates(currency);

        when(productRepositoryMock.existsById(id)).thenReturn(true);

        when(fixerClientMock.getRecentExchangeRateData()).thenReturn(fixerResponse1);

        when(productRepositoryMock.save(Mockito.any(Product.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        mockMvc.perform(put("/products/{id}?coin={coin}", id, coin)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objMapper.writeValueAsString(productRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(productRequest.getName()))
                .andExpect(jsonPath("$.description").value(productRequest.getDescription()))
                .andExpect(jsonPath("$.price").value(productRequest.getPrice() / currency.get(coin)))
                .andExpect(jsonPath("$.category").isNotEmpty());
    }

    @Test
    public void deleteProductByIdTest() throws Exception {
        int id = 666;

        when(productRepositoryMock.existsById(id)).thenReturn(true);

        doNothing().when(productRepositoryMock).deleteById(id);

        mockMvc.perform(delete("/products/{id}", id)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteProductByIncorrectIdTestCase() throws Exception {
        int id = 666;

        when(productRepositoryMock.existsById(id)).thenReturn(false);

        mockMvc.perform(delete("/products/{id}", id)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Product with id = " + id + " not found"));
    }
}
