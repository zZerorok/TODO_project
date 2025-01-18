package project.todo.repository.todo.task;

import org.springframework.data.jpa.repository.JpaRepository;
import project.todo.model.todo.Status;
import project.todo.model.todo.Todo;
import project.todo.model.todo.task.Task;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByTodoId(Long todoId);

    void deleteAllByTodoId(Long todoId);

    Boolean existsByTodoAndStatus(Todo todo, Status status);
}
