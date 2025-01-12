package project.todo.repository.todo;

import org.springframework.data.jpa.repository.JpaRepository;
import project.todo.model.todo.Todo;
import project.todo.model.todo.TodoStatus;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    List<Todo> findAllByMemberId(Long memberId);

    List<Todo> findByMemberIdAndStatus(Long memberId, TodoStatus status);
}
