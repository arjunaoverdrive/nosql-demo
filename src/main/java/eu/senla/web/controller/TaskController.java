package eu.senla.web.controller;

import eu.senla.domain.Task;
import eu.senla.mapper.TaskMapper;
import eu.senla.service.TaskService;
import eu.senla.web.dto.request.AddObserversRequest;
import eu.senla.web.dto.request.TaskSubmitRequest;
import eu.senla.web.dto.request.TaskUpdateRequest;
import eu.senla.web.dto.response.TaskResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TaskController {
    TaskService taskService;
    TaskMapper taskMapper;

    @GetMapping
    public Flux<TaskResponse> getAllTasks(@RequestParam(defaultValue = "10") Integer pageSize,
                                          @RequestParam(defaultValue = "0") Integer page) {
        Flux<Task> tasks = taskService.findAll(pageSize, page);
        return tasks.map(taskMapper::toTaskResponse);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<TaskResponse>> getTaskById(@PathVariable String id) {
        return taskService.findById(id)
                .map(taskMapper::toTaskResponse)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<TaskResponse>> createTask(@RequestBody TaskSubmitRequest request) {
        Task task = taskMapper.toTask(request);
        return taskService.save(task)
                .map(taskMapper::toTaskResponse)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<TaskResponse>> updateTaskById(@PathVariable String id, @Valid @RequestBody TaskUpdateRequest request) {
       Task task = taskMapper.toTask(id, request);
       return taskService.updateTask(task)
               .map(taskMapper::toTaskResponse)
               .map(ResponseEntity::ok)
               .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/observers")
    public Mono<ResponseEntity<TaskResponse>> addObserversToTask(@PathVariable String id, @Valid @RequestBody AddObserversRequest request) {
        Task task = taskMapper.toTask(id, request);
        return taskService.updateTask(task)
                .map(taskMapper::toTaskResponse)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteTask(@PathVariable String id) {
        return taskService.deleteById(id);
    }
}
