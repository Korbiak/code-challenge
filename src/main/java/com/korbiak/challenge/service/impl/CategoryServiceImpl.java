package com.korbiak.challenge.service.impl;

import com.korbiak.challenge.dto.request.CategoryRequest;
import com.korbiak.challenge.dto.response.CategoryResponse;
import com.korbiak.challenge.mapper.CategoryMapper;
import com.korbiak.challenge.model.Category;
import com.korbiak.challenge.repository.CategoryRepository;
import com.korbiak.challenge.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryResponse> findAllCategories() {
        log.info("Getting list of all categories");
        List<Category> categories = categoryRepository.findAll();

        return categories.stream()
                .map(categoryMapper::modelToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse findCategoryById(int id) {
        log.info("Getting category by id = " + id);
        Category category = getCategoryById(id);

        return categoryMapper.modelToResponse(category);
    }

    @Override
    public CategoryResponse saveCategory(CategoryRequest categoryRequest) {
        log.info("Saving new category = " + categoryRequest);
        checkRepeatableName(categoryRequest.getName());
        Category category = categoryMapper.requestToModel(categoryRequest);

        categoryRepository.save(category);

        return categoryMapper.modelToResponse(category);
    }

    @Override
    public CategoryResponse updateCategoryById(int id, CategoryRequest categoryRequest) {
        log.info("Updating category with id =" + id + " to category = " + categoryRequest);
        checkRepeatableName(categoryRequest.getName());
        Category category = getCategoryById(id);

        category = categoryMapper.requestToEntity(categoryRequest, category);
        categoryRepository.save(category);

        return categoryMapper.modelToResponse(category);
    }

    @Override
    public void deleteCategoryById(int id) {
        log.info("Deleting category with id = " + id);
        checkValidCategory(id);
        categoryRepository.deleteById(id);
    }

    private void checkValidCategory(int id) {
        log.info("Checking category with id = " + id);
        if (!categoryRepository.existsById(id)) {
            log.error("Category with id = " + id + " not found");
            throw new EntityNotFoundException("Category with id = " + id + " not found");
        }
    }

    private Category getCategoryById(int id) {
        log.info("Getting category with id = " + id);
        return categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Category with id = " + id + "not found");
                    return new EntityNotFoundException("Category with id = " + id + " not found");
                });
    }

    private void checkRepeatableName(String name) {
        log.info("Checking for the uniqueness of the name = " + name);
        List<Category> categories = categoryRepository.findAll();
        categories.forEach(category -> {
            if (category.getName().equals(name)) {
                log.error(name + " name is not unique");
                throw new IllegalArgumentException("Category with name = " + name + " already exist");
            }
        });
    }
}
