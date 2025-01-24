package eu.senla.service.Impl;

import eu.senla.config.properties.AppCacheProperties;
import eu.senla.dao.UserRepository;
import eu.senla.domain.Task;
import eu.senla.domain.User;
import eu.senla.exception.NotFoundException;
import eu.senla.mapper.UserMapper;
import eu.senla.model.UserWithTasksModel;
import eu.senla.service.UserService;
import eu.senla.utils.BeanUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CacheConfig(cacheManager = "redisCacheManager")
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    MongoTemplate mongoTemplate;
    UserMapper mapper;

    @Override
    @Cacheable(cacheNames = AppCacheProperties.CacheNames.ALL_USERS,
            key = "#pageable.pageNumber + '_' + #pageable.pageSize", unless = "#result == null || #result.isEmpty()")
    public List<User> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).getContent();
    }

    @Override
    public List<User> findAllByIds(Collection<String> ids) {
        return userRepository.findAllById(ids);
    }

    @Override
    @Cacheable(cacheNames = AppCacheProperties.CacheNames.USER_BY_ID, key = "#id", unless = "#result == null")
    public User findById(String id) {
        return userRepository.findById(id).orElseThrow(
                () -> new NotFoundException(
                        MessageFormat.format("User with ID {0} not found", id)
                )
        );
    }

    @Override
    public UserWithTasksModel findUserWithTasksAsAuthor(String id) {

        User user = mongoTemplate.findById(id, User.class);
        UserWithTasksModel userModel = mapper.toUserWithTasks(user);


        if (user == null) {
            return null;
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("author.id").is(id));

        List<Task> tasks = mongoTemplate.find(query, Task.class);
        userModel.setTasks(new HashSet<>(tasks));

        return userModel;
    }

    @Override
    public UserWithTasksModel findUserWithTasksAsAssignee(String id) {

        User user = mongoTemplate.findById(id, User.class);
        UserWithTasksModel userModel = mapper.toUserWithTasks(user);
        if (user == null) {
            return null;
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("assignee.id").is(id));
        List<Task> tasks = mongoTemplate.find(query, Task.class);
        userModel.setTasks(new HashSet<>(tasks));

        return userModel;
    }

    @Override
    public UserWithTasksModel findUserWithTasksAsObserver(String id) {

        User user = mongoTemplate.findById(id, User.class);
        UserWithTasksModel userModel = mapper.toUserWithTasks(user);

        if (user == null) {
            return null;
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("observers.id").is(id));
        List<Task> tasks = mongoTemplate.find(query, Task.class);
        userModel.setTasks(new HashSet<>(tasks));

        return userModel;
    }

    @Override
    @CacheEvict(
            cacheNames = AppCacheProperties.CacheNames.ALL_USERS, allEntries = true
    )
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = AppCacheProperties.CacheNames.ALL_USERS, allEntries = true),
            @CacheEvict(cacheNames = AppCacheProperties.CacheNames.USER_BY_ID, key = "#user.id", beforeInvocation = true)
    })
    public User updateUser(User user) {
        User fromDb = findById(user.getId());
        BeanUtils.copyNonNullValues(user, fromDb);
        return userRepository.save(user);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = AppCacheProperties.CacheNames.ALL_USERS, allEntries = true),
            @CacheEvict(cacheNames = AppCacheProperties.CacheNames.USER_BY_ID, key = "#id")
    })
    public void deleteUserById(String id) {
        User toDelete = findById(id);
        userRepository.delete(toDelete);
    }
}
