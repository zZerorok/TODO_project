package project.todo.repository.todo;

import org.springframework.data.jpa.repository.JpaRepository;
import project.todo.model.todo.Todo;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    List<Todo> findAllByWriterId(Long memberId);
}
