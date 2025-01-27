package project.todo.service.todo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.todo.exception.member.MemberException;
import project.todo.exception.todo.TodoNotFoundException;
import project.todo.model.todo.Status;
import project.todo.model.todo.Todo;
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

    public List<TodoResponse> findTodos(Optional<Status> status) {
        var loginMember = getLoginMember();
        var todos = getTodos(loginMember);

        if (status.isEmpty()) {
            return toResponse(todos);
        }

        var todosByStatus = filterByStatus(todos, status.get());
        return toResponse(todosByStatus);
    }

    public TodoWithTasksResponse getTodoWithTasks(Long todoId) {
        var loginMember = getLoginMember();
        var todo = getTodoWithValidation(loginMember, todoId);
        var tasks = getTasks(todo);

        return TodoWithTasksResponse.from(todo, tasks);
    }

    private LoginMember getLoginMember() {
        var loginMember = sessionHolder.getSession();

        if (loginMember == null) {
            throw new MemberException("로그인이 필요합니다.");
        }

        return loginMember;
    }

    private List<Todo> getTodos(LoginMember loginMember) {
        return todoRepository.findAllByMemberId(loginMember.id());
    }

    private List<Todo> filterByStatus(List<Todo> todos, Status status) {
        return todos.stream()
                .filter(todo -> todo.getStatus().equals(status))
                .toList();
    }

    private List<TodoResponse> toResponse(List<Todo> todos) {
        return todos.stream()
                .map(TodoResponse::from)
                .toList();
    }

    private Todo getTodoWithValidation(LoginMember loginMember, long todoId) {
        var todo = getTodo(todoId);
        todo.validateMember(loginMember.id());
        return todo;
    }

    private Todo getTodo(long todoId) {
        return todoRepository.findById(todoId)
                .orElseThrow(() -> new TodoNotFoundException("해당 Todo가 존재하지 않습니다."));
    }

    private List<Task> getTasks(Todo todo) {
        return taskRepository.findAllByTodoId(todo.getId());
    }
}
