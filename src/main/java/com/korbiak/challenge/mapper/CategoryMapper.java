package com.korbiak.challenge.mapper;

import com.korbiak.challenge.dto.request.CategoryRequest;
import com.korbiak.challenge.dto.response.CategoryResponse;
import com.korbiak.challenge.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category requestToModel(CategoryRequest categoryRequest);

    CategoryResponse modelToResponse(Category category);

    Category requestToEntity(CategoryRequest dto, @MappingTarget Category category);

    Category responseToModel(CategoryResponse categoryResponse);
}
