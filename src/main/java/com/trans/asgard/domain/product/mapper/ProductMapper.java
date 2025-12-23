package com.trans.asgard.domain.product.mapper;

import com.trans.asgard.domain.iam.dto.ProductDto;
import com.trans.asgard.domain.product.model.Product;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDto toDto(Product product);
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    Product toEntity(ProductDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ProductDto dto, @MappingTarget Product entity);
}
