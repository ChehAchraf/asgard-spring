package com.trans.asgard.domain.iam.mapper;

import com.trans.asgard.domain.iam.dto.RegisterUserRequest;
import com.trans.asgard.domain.iam.dto.UserResponse;
import com.trans.asgard.domain.iam.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id",ignore = true)

    User toEntity(RegisterUserRequest request);

    UserResponse toResponse(User user);

}