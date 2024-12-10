package eu.senla.mapper;

import eu.senla.domain.Task;
import eu.senla.model.TaskModel;
import eu.senla.web.dto.request.AddObserversRequest;
import eu.senla.web.dto.request.TaskSubmitRequest;
import eu.senla.web.dto.request.TaskUpdateRequest;
import eu.senla.web.dto.response.TaskResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {
    TaskResponse toTaskResponse(Task task);

    Task toTask(TaskSubmitRequest request);
    Task toTask(TaskUpdateRequest request);
    default Task toTask(String id, TaskUpdateRequest request){
        Task task = toTask(request);
        task.setId(id);
        return task;
    }

    Task toTask(AddObserversRequest request);
    default Task toTask(String id, AddObserversRequest request){
        Task task = toTask(request);
        task.setId(id);
        return task;
    }

    TaskModel toModel(Task task);
}