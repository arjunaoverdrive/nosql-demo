package eu.senla.service;

import eu.senla.domain.User;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
public interface UserService {
    List<User> findAllUsers(Pageable pageable);
    List<User> findAllByIds(Collection<String> ids);
    User findById(String id);
    User createUser(User user);
    User updateUser(User user);
    void deleteUserById(String id);
}