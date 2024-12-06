package eu.senla.web.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskSubmitRequest {

    @NotEmpty
    String authorId;
    @NotEmpty
    @Size(min = 3, max = 32)
    String name;
    @Size(max = 1024)
    String description;
    String assigneeId;
    @Builder.Default
    Set<String> observerIds = new HashSet<>();
}
