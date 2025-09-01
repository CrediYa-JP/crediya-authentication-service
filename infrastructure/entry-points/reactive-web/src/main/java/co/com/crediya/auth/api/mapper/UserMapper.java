package co.com.crediya.auth.api.mapper;

import co.com.crediya.auth.api.dto.request.RegisterUserRequest;
import co.com.crediya.auth.api.dto.response.UserResponse;
import co.com.crediya.auth.model.constants.RoleConstants;
import co.com.crediya.auth.model.user.User;

public final class UserMapper {

    private UserMapper() {
    }

    public static User toUser(RegisterUserRequest request) {
        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .identityDocument(request.getIdentityDocument())
                .phone(request.getPhone())
                .baseSalary(request.getBaseSalary())
                .password(request.getPassword())
                .roleId(RoleConstants.CLIENT_ROLE_ID)
                .build();
    }

    public static UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .identityDocument(user.getIdentityDocument())
                .phone(user.getPhone())
                .baseSalary(user.getBaseSalary())
                .creationDate(user.getCreationDate())
                .build();
    }
}