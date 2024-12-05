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
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Document(collection = "tasks")
public class Task implements Serializable {

    @Id
    String id;
    String name;
    String description;
    Instant createdAt;
    Instant updatedAt;
    @Builder.Default
    TaskStatus status = TaskStatus.TODO;
    User author;
    User assignee;

    @DocumentReference(collection = "users", lookup = "{'user':?#{#self._id} }")
    Set<User> observers = new HashSet<>();
}