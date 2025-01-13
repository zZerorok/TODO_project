package project.todo.service.todo;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import project.todo.model.todo.Todo;
import project.todo.model.todo.TodoStatus;
import project.todo.model.todo.task.Task;
import project.todo.repository.todo.TodoRepository;
import project.todo.repository.todo.task.TaskRepository;
import project.todo.service.security.LoginMember;
import project.todo.service.security.SessionHolder;
import project.todo.service.todo.dto.TodoResponse;
import project.todo.service.todo.dto.TodoWithTasksResponse;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TodoReadService {
    private final TodoRepository todoRepository;
    private final TaskRepository taskRepository;
    private final SessionHolder sessionHolder;

    public List<TodoResponse> findTodos(Optional<TodoStatus> todoStatus) {
        var loginMember = getLoginMember();

        if (todoStatus.isEmpty()) {
            var todos = getTodos(loginMember.id());
            return toResponse(todos);
        }

        var todosByStatus = getTodosByStatus(loginMember.id(), todoStatus.get());
        return toResponse(todosByStatus);
    }

    public TodoWithTasksResponse getTodoWithTasks(Long todoId) {
        var loginMember = getLoginMember();
        var todo = getTodoWithValidation(todoId, loginMember);
        var tasks = getTasks(todo);

        return TodoWithTasksResponse.from(
                todo,
                tasks
        );
    }

    private LoginMember getLoginMember() {
        var loginMember = sessionHolder.getSession();

        if (loginMember == null) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        return loginMember;
    }

    private List<Todo> getTodos(long memberId) {
        return todoRepository.findAllByMemberId(memberId);
    }

    private List<Todo> getTodosByStatus(long memberId, TodoStatus status) {
        return todoRepository.findByMemberIdAndStatus(memberId, status);
    }

    private List<TodoResponse> toResponse(List<Todo> todos) {
        return todos.stream()
                .map(TodoResponse::from)
                .toList();
    }

    private Todo getTodoWithValidation(long todoId, LoginMember loginMember) {
        var todo = getTodo(todoId);
        validateTodoOwnership(todo, loginMember);
        return todo;
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

    private void validateTodoOwnership(Todo todo, LoginMember loginMember) {
        if (!todo.getMemberId().equals(loginMember.id())) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "해당 Todo에 접근 권한이 없습니다.");
        }
    }
}
