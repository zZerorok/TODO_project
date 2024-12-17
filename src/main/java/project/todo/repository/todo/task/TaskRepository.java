package project.todo.repository.todo.task;

import org.springframework.data.jpa.repository.JpaRepository;
import project.todo.model.todo.task.Task;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByTodoId(Long todoId);
}
