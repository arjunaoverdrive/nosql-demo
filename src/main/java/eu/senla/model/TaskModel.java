package eu.senla.model;

import eu.senla.domain.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskModel {

    Long id;
    String name;
    String description;
    TaskStatus status;
    UserModel author;
    UserModel assignee;
    @Builder.Default
    Set<UserModel> observers = new HashSet<>();
}