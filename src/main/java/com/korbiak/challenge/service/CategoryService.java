package com.korbiak.challenge.service;

import com.korbiak.challenge.dto.request.CategoryRequest;
import com.korbiak.challenge.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    List<CategoryResponse> findAllCategories();

    CategoryResponse findCategoryById(int id);

    CategoryResponse saveCategory(CategoryRequest categoryRequest);

    CategoryResponse updateCategoryById(int id, CategoryRequest categoryRequest);

    void deleteCategoryById(int id);
}
