package com.korbiak.challenge.mapper;

import com.korbiak.challenge.dto.request.ProductRequest;
import com.korbiak.challenge.dto.response.ProductResponse;
import com.korbiak.challenge.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product requestToModel(ProductRequest productResponse);

    ProductResponse modelToResponse(Product product);

    Product requestToEntity(ProductRequest dto, @MappingTarget Product product);
}
