package eu.senla.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.io.Serializable;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "users")
@Builder
public class User implements Serializable {

    @Id
    String id;
    String username;
    String email;
    String password;
    @Builder.Default
    Boolean isEnabled = true;

    @DocumentReference(collection = "tasks", lookup = "{'task':?#{#self._id} }", lazy = false)
    Set<Task> createdTasks;

    @DocumentReference(collection = "tasks", lookup = "{'task':?#{#self._id} }", lazy = false)
    Set<Task> assignedTasks;

    @DocumentReference(collection = "tasks", lookup = "{'task':?#{#self._id} }", lazy = false)
    Set<Task> observedTasks;
}
