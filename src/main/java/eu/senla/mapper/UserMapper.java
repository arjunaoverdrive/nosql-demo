package eu.senla.mapper;

import eu.senla.domain.User;
import eu.senla.model.UserModel;
import eu.senla.model.UserWithTasksModel;
import eu.senla.web.dto.request.UserRequest;
import eu.senla.web.dto.response.UserListResponse;
import eu.senla.web.dto.response.UserResponse;
import eu.senla.web.dto.response.UserWithTasksResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.Collection;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserResponse toUserResponse(User user);
    default UserListResponse toListResponse(Collection<User> users) {
        return UserListResponse.builder()
                .users(users.stream()
                        .map(this::toUserResponse)
                        .toList())
                .build();
    }
    User toUser(UserRequest request);
    default User toUser(String id, UserRequest request) {
        User user = toUser(request);
        user.setId(id);
        return user;
    }

    UserModel toModel (User user);

    UserWithTasksModel toUserWithTasks(User user);
    UserWithTasksResponse toUserWithTasks(UserWithTasksModel user);

}
