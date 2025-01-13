package project.todo.service.todo;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import project.todo.model.todo.Todo;
import project.todo.model.todo.task.Task;
import project.todo.repository.todo.TodoRepository;
import project.todo.repository.todo.task.TaskRepository;
import project.todo.service.security.LoginMember;
import project.todo.service.security.SessionHolder;
import project.todo.service.todo.dto.TodoCreateRequest;
import project.todo.service.todo.dto.TodoUpdateRequest;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class TodoWriteService {
    private final TodoRepository todoRepository;
    private final TaskRepository taskRepository;
    private final SessionHolder sessionHolder;

    public void create(TodoCreateRequest request) {
        var loginMember = getLoginMember();
        var todo = new Todo(
                loginMember.id(),
                request.title(),
                request.deadLine()
        );

        todoRepository.save(todo);
    }

    public void update(Long todoId, TodoUpdateRequest request) {
        var loginMember = getLoginMember();
        var todo = getTodo(todoId, loginMember.id());

        todo.update(request.title(), request.deadline());
    }

    public void complete(Long todoId) {
        var loginMember = getLoginMember();
        var tasks = getTasks(todoId);

        if (isAllTasksCompleted(tasks)) {
            var todo = getTodo(todoId, loginMember.id());
            todo.complete();
        }
    }

    public void incomplete(Long todoId) {
        var loginMember = getLoginMember();
        var todo = getTodo(todoId, loginMember.id());

        todo.incomplete();
    }

    public void delete(Long todoId) {
        var loginMember = getLoginMember();
        var todo = getTodo(todoId, loginMember.id());

        taskRepository.deleteAllByTodoId(todo.getId());
        todoRepository.delete(todo);
    }

    private LoginMember getLoginMember() {
        var loginMember = sessionHolder.getSession();

        if (loginMember == null) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        return loginMember;
    }

    private Todo getTodo(long todoId, long memberId) {
        return todoRepository.findByIdAndMemberId(todoId, memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당 Todo가 존재하지 않습니다."));
    }

    private List<Task> getTasks(long todoId) {
        var tasks = taskRepository.findAllByTodoId(todoId);

        if (tasks.isEmpty()) {
            throw new IllegalStateException("Task를 생성해주세요.");
        }

        return tasks;
    }

    private boolean isAllTasksCompleted(List<Task> tasks) {
        return tasks.stream()
                .allMatch(it -> it.getStatus().isCompleted());
    }
}
