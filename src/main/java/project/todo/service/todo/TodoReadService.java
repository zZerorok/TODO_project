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
import project.todo.service.security.dto.LoginMember;
import project.todo.service.security.SessionHolder;
import project.todo.service.todo.dto.TodoResponse;
import project.todo.service.todo.dto.TodoWithTasksResponse;

import java.util.List;
import java.util.Optional;

/**
 * Todo 읽기 작업 요청을 처리하는 서비스 클래스
 */
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TodoReadService {
    private final TodoRepository todoRepository;
    private final TaskRepository taskRepository;
    private final SessionHolder sessionHolder;

    /**
     * 요청 상태에 따라 Todo 목록을 조회합니다.<p>
     *
     * - 상태값이 주어지지 않으면 전체 Todo를 반환합니다.<p>
     * - 상태값이 존재하면 해당 상태(완료 또는 미완료)에 해당하는 Todo만 필터링하여 반환합니다.<p>
     *
     * @param status Todo의 상태 (완료 또는 미완료)
     * @return {@link List<TodoResponse>} Todo 목록 객체
     */
    public List<TodoResponse> findTodos(Optional<Status> status) {
        var loginMember = getLoginMember();
        var todos = getTodos(loginMember);

        if (status.isEmpty()) {
            return toResponse(todos);
        }

        var todosByStatus = filterByStatus(todos, status.get());
        return toResponse(todosByStatus);
    }

    /**
     * 특정 Todo와 해당 Todo에 포함된 모든 Task를 조회합니다.
     *
     * @param todoId - 조회를 요청한 Todo의 ID
     * @return {@link TodoWithTasksResponse} 특정 Todo와 포함된 전체 Task 포함한 객체
     */
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
        return todoRepository.findAllByWriterId(loginMember.id());
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
        todo.validateWriter(loginMember.id());
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
