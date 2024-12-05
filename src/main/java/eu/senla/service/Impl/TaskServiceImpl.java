package eu.senla.service.Impl;

import eu.senla.config.properties.AppCacheProperties;
import eu.senla.dao.TaskRepository;
import eu.senla.domain.Task;
import eu.senla.domain.User;
import eu.senla.exception.NotFoundException;
import eu.senla.service.PublishingService;
import eu.senla.service.TaskService;
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
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CacheConfig(cacheManager = "redisCacheManager")
public class TaskServiceImpl implements TaskService {
    TaskRepository taskRepository;
    UserService userService;
    PublishingService publishingService;

    @Override
    @Cacheable(cacheNames = AppCacheProperties.CacheNames.ALL_TASKS, key = "#pageable.pageNumber + '_' + #pageable.pageSize",
            unless = "#result == null || #result.isEmpty()")
    public List<Task> findAll(Pageable pageable) {
        return taskRepository.findAll(pageable).getContent();
    }

    @Override
    @Cacheable(cacheNames = AppCacheProperties.CacheNames.TASK_BY_ID, key = "#id", unless = "#result == null")
    public Task findById(String id) {
        return taskRepository.findById(id).orElseThrow(
                () -> new NotFoundException(
                        MessageFormat.format("Task with ID {0} not found", id)
                )
        );
    }

    @Override
    @CacheEvict(value = AppCacheProperties.CacheNames.ALL_TASKS, allEntries = true)
    public Task save(Task task) {
        return taskRepository.save(task);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = AppCacheProperties.CacheNames.ALL_TASKS, allEntries = true),
            @CacheEvict(value = AppCacheProperties.CacheNames.TASK_BY_ID, key = "#id", beforeInvocation = true, cacheResolver = "customCacheResolver")
    })
    public void deleteById(String  id) {
        Task toDelete = findById(id);
        toDelete.getObservers().forEach(u -> u.removeObservedTask(toDelete));
        taskRepository.delete(toDelete);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = AppCacheProperties.CacheNames.ALL_TASKS, allEntries = true, beforeInvocation = true),
            @CacheEvict(value = AppCacheProperties.CacheNames.TASK_BY_ID, key = "#task.id", beforeInvocation = true)
    })
    public Task updateTask(Task task) {
        Task fromDb = findById(task.getId());
        BeanUtils.copyNonNullValues(task, fromDb);
        publishingService.publish(fromDb);
        return taskRepository.save(fromDb);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = AppCacheProperties.CacheNames.ALL_TASKS, allEntries = true),
            @CacheEvict(value = AppCacheProperties.CacheNames.TASK_BY_ID, key = "#id", beforeInvocation = true)
    })
    public Task addObservers(String id, List<String> observerIds) {
        List<User> allByIds = userService.findAllByIds(observerIds);
        Task task = findById(id);
        allByIds.forEach(u -> u.addObservedTasks(task));
        return taskRepository.save(task);
    }
}