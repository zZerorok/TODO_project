package project.todo.service.todo;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.todo.model.member.Member;
import project.todo.model.todo.Todo;
import project.todo.model.todo.task.Task;
import project.todo.repository.member.MemberRepository;
import project.todo.repository.todo.TodoRepository;
import project.todo.repository.todo.task.TaskRepository;
import project.todo.service.todo.dto.TodoSimpleResponse;
import project.todo.service.todo.dto.TodoWithTasksResponse;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TodoReadService {
    private final TodoRepository todoRepository;
    private final MemberRepository memberRepository;
    private final TaskRepository taskRepository;

    public List<TodoSimpleResponse> findTodos(Long memberId) {
        var member = getMember(memberId);
        var todos = getTodos(member.getId());

        return todos.stream()
                .map(TodoSimpleResponse::from)
                .toList();
    }

    public TodoWithTasksResponse getTodoWithTasks(Long todoId) {
        var todo = getTodo(todoId);
        var tasks = getTasks(todo);

        return TodoWithTasksResponse.from(
                todo,
                tasks
        );
    }

    private Member getMember(long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
    }

    private List<Todo> getTodos(long memberId) {
        return todoRepository.findAllByMemberId(memberId);
    }

    private Todo getTodo(long todoId) {
        return todoRepository.findById(todoId)
                .orElseThrow(() -> new EntityNotFoundException("해당 Todo가 존재하지 않습니다."));
    }

    private List<Task> getTasks(Todo todo) {
        var tasks = taskRepository.findAllByTodoId(todo.getId());

        if (tasks.isEmpty()) {
            throw new EntityNotFoundException("해당 Todo에 대한 Task가 존재하지 않습니다.");
        }

        return tasks;
    }
}
