package eu.senla.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "users")
public class User implements Serializable {

    @Id
    String id;
    String username;
    String email;
    String password;
    boolean isEnabled;

    @DocumentReference(collection = "tasks", lookup = "{'task':?#{#self._id} }", lazy = false)
    Set<Task> createdTasks = new HashSet<>();

    @DocumentReference(collection = "tasks", lookup = "{'task':?#{#self._id} }", lazy = false)
    Set<Task> assignedTasks = new HashSet<>();

    @DocumentReference(collection = "tasks", lookup = "{'task':?#{#self._id} }", lazy = false)
    Set<Task> observedTasks = new HashSet<>();

    public void addCreatedTask(Task task) {
        this.createdTasks.add(task);
        task.setAuthor(this);
    }

    public void addAssignedTask(Task task) {
        this.assignedTasks.add(task);
        task.setAssignee(this);
    }

    public void addObservedTasks(Task task) {
        task.getObservers().add(this);
        this.observedTasks.add(task);
    }

    public void removeObservedTask(Task task) {
        this.observedTasks.remove(task);
        task.setObservers(new HashSet<>());
    }
}
