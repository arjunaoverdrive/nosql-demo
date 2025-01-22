package eu.senla.service;

import eu.senla.domain.Task;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

public interface TaskService {
    Flux<Task> findAll(Integer pageSize, Integer page);
    Mono<Task> findById(String id);
    Mono<Task> save(Task task);
    Mono<Void> deleteById(String id);
    Mono<Task> updateTask(Task task);
    Mono<Task> addObservers(String id, Set<String> observerIds);
}
