package eu.senla.service;

import eu.senla.domain.Task;
import eu.senla.web.dto.response.TaskResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
public interface TaskService {
    List<Task> findAll(Pageable pageable);
    Task findById(String id);
    List<TaskResponse> findTaskById(String id);
    Task save(Task task);
    void deleteById(String id);
    Task updateTask(Task task);
    Task addObservers(String id, List<String> observerIds);
}
