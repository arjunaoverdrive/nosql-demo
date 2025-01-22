package eu.senla.dao;

import eu.senla.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = {"createdTasks", "assignedTasks", "observedTasks"})
    Page<User> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"createdTasks", "assignedTasks", "observedTasks"})
    Optional<User> findById(Long aLong);
}
