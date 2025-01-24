package eu.senla.dao;

import eu.senla.domain.Task;
import eu.senla.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends MongoRepository<Task, String> {

    @Query("{ '$or': [ { 'author.id': ?0 }, { 'assignee.id': ?0 }, { 'observer.id': ?0 } ] }")
    List<Task> findByUser(String userId);
}
