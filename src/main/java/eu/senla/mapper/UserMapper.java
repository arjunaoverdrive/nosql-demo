package eu.senla.mapper;

import eu.senla.domain.User;
import eu.senla.model.UserModel;
import eu.senla.web.dto.request.UserRequest;
import eu.senla.web.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserResponse toUserResponse(User user);

    User toUser(UserRequest request);

    default User toUser(String id, UserRequest request){
        User user = toUser(request);
        user.setId(id);
        return user;
    }

    UserModel toModel (User user);
}
