package eu.senla.service.Impl;

import eu.senla.config.properties.AppCacheProperties;
import eu.senla.dao.UserRepository;
import eu.senla.domain.User;
import eu.senla.service.UserService;
import eu.senla.utils.BeanUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CacheConfig(cacheManager = "redisCacheManager")
public class UserServiceImpl implements UserService {

    UserRepository userRepository;

    @Override
    @Cacheable(cacheNames = AppCacheProperties.CacheNames.ALL_USERS, unless = "#result == null || #result.isEmpty()")
    public Flux<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Mono<Set<User>> findAllByIds(Collection<String> ids) {
        return userRepository.findAllById(ids).collect(Collectors.toSet());
    }

    @Override
    @Cacheable(cacheNames = AppCacheProperties.CacheNames.USER_BY_ID, key = "#id", unless = "#result == null")
    public Mono<User> findById(String id) {
        return userRepository.findById(id);
    }

    @Override
    @CacheEvict(
            cacheNames = AppCacheProperties.CacheNames.ALL_USERS, allEntries = true
    )
    public Mono<User> createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = AppCacheProperties.CacheNames.ALL_USERS, allEntries = true),
            @CacheEvict(cacheNames = AppCacheProperties.CacheNames.USER_BY_ID, key = "#user.id", beforeInvocation = true)
    })
    public Mono<User> updateUser(User user) {
        return findById(user.getId())
                .flatMap(fromDb -> {
                    BeanUtils.copyNonNullValues(user, fromDb);
                    return userRepository.save(fromDb);
                });

    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = AppCacheProperties.CacheNames.ALL_USERS, allEntries = true),
            @CacheEvict(cacheNames = AppCacheProperties.CacheNames.USER_BY_ID, key = "#id")
    })
    public Mono<Void> deleteUserById(String id) {
        return userRepository.deleteById(id);
    }
}
