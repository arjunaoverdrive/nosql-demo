package eu.senla.dao;

import eu.senla.domain.Task;
import eu.senla.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends MongoRepository<Task, String> {

    List<Task> findByAuthor(User author);
}
