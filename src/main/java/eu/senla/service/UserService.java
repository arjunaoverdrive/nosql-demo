package eu.senla.service;

import eu.senla.domain.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Set;

public interface UserService {
    Flux<User> findAllUsers();

    Mono<Set<User>> findAllByIds(Collection<String> ids);

    Mono<User> findById(String id);

    Mono<User> createUser(User user);

    Mono<User> updateUser(User user);

    Mono<Void> deleteUserById(String id);
}