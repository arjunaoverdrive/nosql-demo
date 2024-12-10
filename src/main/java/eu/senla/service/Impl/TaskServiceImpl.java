package eu.senla.service.Impl;

import eu.senla.config.properties.AppCacheProperties;
import eu.senla.dao.TaskRepository;
import eu.senla.domain.Task;
import eu.senla.domain.TaskStatus;
import eu.senla.domain.User;
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
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CacheConfig(cacheManager = "redisCacheManager")
public class TaskServiceImpl implements TaskService {

    TaskRepository taskRepository;
    UserService userService;
    PublishingService publishingService;


    @Override
    @Cacheable(cacheNames = AppCacheProperties.CacheNames.ALL_TASKS, unless = "#result == null || #result.isEmpty()")
    public Flux<Task> findAll() {
        Flux<Task> tasks = taskRepository.findAll();
        return tasks.flatMap(task -> {
            Mono<Task> taskMono = Mono.just(task);
            return zipTaskMonoWithUserMonos(taskMono);
        });
    }

    @Override
    @Cacheable(cacheNames = AppCacheProperties.CacheNames.TASK_BY_ID, key = "#id", unless = "#result == null")
    public Mono<Task> findById(String id) {
        Mono<Task> task = taskRepository.findById(id);
        return zipTaskMonoWithUserMonos(task);
    }

    @Override
    @CacheEvict(value = AppCacheProperties.CacheNames.ALL_TASKS, allEntries = true)
    public Mono<Task> save(Task task) {
        task.setAssigneeId(task.getAssigneeId() == null ? task.getAuthorId() : task.getAssigneeId());
        task.setCreatedAt(Instant.now());
        task.setUpdatedAt(Instant.now());
        task.setStatus(TaskStatus.TODO);

        Mono<Task> taskMono = Mono.just(task);

        return zipTaskMonoWithUserMonos(taskMono).flatMap(taskRepository::save);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = AppCacheProperties.CacheNames.ALL_TASKS, allEntries = true),
            @CacheEvict(value = AppCacheProperties.CacheNames.TASK_BY_ID, key = "#id", beforeInvocation = true, cacheResolver = "customCacheResolver")
    })
    public Mono<Void> deleteById(String id) {
        return taskRepository.deleteById(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = AppCacheProperties.CacheNames.ALL_TASKS, allEntries = true, beforeInvocation = true),
            @CacheEvict(value = AppCacheProperties.CacheNames.TASK_BY_ID, key = "#task.id", beforeInvocation = true)
    })
    public Mono<Task> updateTask(Task task) {
        Mono<Task> taskMono = findById(task.getId()).map(fromDb -> {
            BeanUtils.copyNonNullValues(task, fromDb);
            publishingService.publish(task);
            return fromDb;
        });
        return zipTaskMonoWithUserMonos(taskMono).flatMap(taskRepository::save);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = AppCacheProperties.CacheNames.ALL_TASKS, allEntries = true),
            @CacheEvict(value = AppCacheProperties.CacheNames.TASK_BY_ID, key = "#id", beforeInvocation = true)
    })
    public Mono<Task> addObservers(String id, Set<String> observerIds) {
        Mono<Task> task = findById(id).map(t -> {
            t.setObserverIds(observerIds);
            return t;
        });
        return zipTaskMonoWithUserMonos(task).flatMap(taskRepository::save);
    }

    private Mono<Task> zipTaskMonoWithUserMonos(Mono<Task> taskMono) {
        Mono<User> authorMono = taskMono.map(Task::getAuthorId)
                .flatMap(userService::findById);
        Mono<User> assigneeMono = taskMono.map(Task::getAssigneeId)
                .flatMap(userService::findById);
        Mono<Set<User>> observersMono = taskMono.map(Task::getObserverIds)
                .flatMap(userService::findAllByIds);

        return Mono.zip(taskMono, authorMono, assigneeMono, observersMono)
                .flatMap(data -> {
                    Task t1 = data.getT1();
                    t1.setAuthor(data.getT2());
                    t1.setAssignee(data.getT3());
                    t1.setObservers(data.getT4());
                    return Mono.just(t1);
                });
    }
}