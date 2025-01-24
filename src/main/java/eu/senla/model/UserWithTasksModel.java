package eu.senla.model;

import eu.senla.domain.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserWithTasksModel {

    String id;
    String username;
    String email;
    Boolean isEnabled;
    Set<Task> tasks;
}
