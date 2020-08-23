package com.korbiak.challenge.service;

import com.korbiak.challenge.dto.request.CategoryRequest;
import com.korbiak.challenge.dto.response.CategoryResponse;
import com.korbiak.challenge.mapper.CategoryMapper;
import com.korbiak.challenge.model.Category;
import com.korbiak.challenge.repository.CategoryRepository;
import com.korbiak.challenge.service.impl.CategoryServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class CategoryServiceUnitTest {

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepositoryMock;

    @Mock
    private CategoryMapper categoryMapperMock;

    private final Category category = new Category(
            523,
            "name",
            null
    );

    private final CategoryRequest categoryRequest = new CategoryRequest(
            "name"
    );

    @Before
    public void setUpMocks() {
        MockitoAnnotations.initMocks(this);

        when(categoryMapperMock.requestToModel(Mockito.any(CategoryRequest.class)))
                .thenAnswer(invocationOnMock -> {
                    CategoryRequest categoryRequest = invocationOnMock.getArgument(0);
                    return new Category(
                            0,
                            categoryRequest.getName(),
                            null
                    );
                });
        when(categoryMapperMock.modelToResponse(Mockito.any(Category.class)))
                .thenAnswer(invocationOnMock -> {
                    Category category = invocationOnMock.getArgument(0);
                    return new CategoryResponse(
                            category.getId(),
                            category.getName()
                    );
                });

        when(categoryMapperMock.requestToEntity(Mockito.any(CategoryRequest.class), Mockito.any(Category.class)))
                .thenAnswer(invocationOnMock -> {
                    CategoryRequest categoryRequest = invocationOnMock.getArgument(0);
                    Category category = invocationOnMock.getArgument(1);
                    category.setName(categoryRequest.getName());
                    return category;
                });

        when(categoryRepositoryMock.findAll())
                .thenReturn(Collections.singletonList(category));
    }

    @Test
    public void findAllCategoriesTest() {
        List<CategoryResponse> answer = categoryService.findAllCategories();

        assertTrue(answer.size() > 0);
        assertEquals(answer.get(0).getId(), category.getId());
        assertEquals(answer.get(0).getName(), category.getName());
    }

    @Test
    public void findCategoryByIdTest() {
        int id = 23;

        when(categoryRepositoryMock.findById(id))
                .thenAnswer(invocationOnMock -> {
                    Category category1 = category;
                    category1.setId(invocationOnMock.getArgument(0));
                    return Optional.of(category1);
                });

        CategoryResponse answer = categoryService.findCategoryById(id);

        assertNotNull(answer);
        assertEquals(answer.getId(), id);
        assertEquals(answer.getName(), category.getName());
    }

    @Test
    public void findCategoryByIncorrectIdTest() {
        int id = 23;

        assertThrows(EntityNotFoundException.class, () -> categoryService.findCategoryById(id));
    }

    @Test
    public void saveCategoryTest() {
        int generatedId = 23;
        CategoryRequest testCategory = categoryRequest;
        category.setName("name2");

        when(categoryRepositoryMock.save(Mockito.any(Category.class)))
                .thenAnswer(invocationOnMock -> {
                    Category category = invocationOnMock.getArgument(0);
                    category.setId(generatedId);
                    return category;
                });

        CategoryResponse answer = categoryService.saveCategory(testCategory);

        assertNotNull(answer);
        assertEquals(generatedId, answer.getId());
        assertEquals(answer.getName(), testCategory.getName());
    }

    @Test
    public void saveCategoryWithRepeatableNameTest() {
        assertThrows(IllegalArgumentException.class,
                () -> categoryService.saveCategory(categoryRequest));
    }

    @Test
    public void updateCategoryByIdTest() {
        int id = 777;
        when(categoryRepositoryMock.findAll())
                .thenReturn(new ArrayList<>());

        when(categoryRepositoryMock.findById(id))
                .thenAnswer(invocationOnMock -> {
                    Category category1 = category;
                    category1.setId(invocationOnMock.getArgument(0));
                    return Optional.of(category1);
                });

        when(categoryRepositoryMock.save(Mockito.any(Category.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        CategoryResponse answer = categoryService.updateCategoryById(id, categoryRequest);

        assertNotNull(answer);
        assertEquals(answer.getId(), id);
        assertEquals(answer.getName(), categoryRequest.getName());
    }

    @Test
    public void deleteCategoryByIdTest() {
        int id = 228;

        when(categoryRepositoryMock.existsById(id))
                .thenReturn(true);
        doNothing().when(categoryRepositoryMock).deleteById(id);

        categoryService.deleteCategoryById(id);
    }

    @Test
    public void deleteCategoryByIncorrectIdTest() {
        int id = 228;

        when(categoryRepositoryMock.existsById(id))
                .thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> categoryService.deleteCategoryById(id));
    }
}
