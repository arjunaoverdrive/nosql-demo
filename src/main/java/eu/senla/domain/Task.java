package eu.senla.domain;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
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
    @Indexed(name = "tasks_idx")
    String name;
    String description;
    @CreatedDate
    Instant createdAt;
    @LastModifiedDate
    Instant updatedAt;
    @Builder.Default
    TaskStatus status = TaskStatus.TODO;
    User author;
    User assignee;

    @DocumentReference(collection = "users", lookup = "{'user':?#{#self._id} }")
    @Builder.Default
    Set<User> observers = new HashSet<>();
}