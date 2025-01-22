package eu.senla.dao;

import eu.senla.domain.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    @EntityGraph(attributePaths = {"author", "assignee", "observers"})
    Optional<Task> findById(Long id);

    @EntityGraph(attributePaths = {"author", "assignee", "observers"})
    Page<Task> findAll(Pageable pageable);
}
