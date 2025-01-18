package project.todo.service.todo.task;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import project.todo.model.todo.Status;
import project.todo.model.todo.Todo;
import project.todo.model.todo.task.Task;
import project.todo.repository.todo.TodoRepository;
import project.todo.repository.todo.task.TaskRepository;
import project.todo.service.security.LoginMember;
import project.todo.service.security.SessionHolder;
import project.todo.service.todo.task.dto.TaskAddRequest;
import project.todo.service.todo.task.dto.TaskUpdateRequest;

@RequiredArgsConstructor
@Transactional
@Service
public class TaskWriteService {
    private final TaskRepository taskRepository;
    private final TodoRepository todoRepository;
    private final SessionHolder sessionHolder;

    public void add(Long todoId, TaskAddRequest request) {
        var todo = getTodoWithValidation(todoId);
        var task = new Task(todo, request.content());

        taskRepository.save(task);
    }

    public void update(Long taskId, TaskUpdateRequest request) {
        var task = getTaskWithValidation(taskId);

        task.update(request.content());
    }

    public void complete(Long todoId, Long taskId) {
        var task = getTaskWithValidation(taskId);
        task.complete();

        var todo = getTodoWithValidation(todoId);
        if (isAllTasksCompleted(todo)) {
            todo.complete();
        }
    }

    public void incomplete(Long todoId, Long taskId) {
        var task = getTaskWithValidation(taskId);
        task.incomplete();

        var todo = getTodoWithValidation(todoId);
        if (isCompleted(todo)) {
            todo.incomplete();
        }
    }

    public void delete(Long taskId) {
        var task = getTaskWithValidation(taskId);

        taskRepository.delete(task);
    }

    private LoginMember getLoginMember() {
        var loginMember = sessionHolder.getSession();

        if (loginMember == null) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        return loginMember;
    }

    private Todo getTodoWithValidation(long todoId) {
        var loginMember = getLoginMember();
        var todo = getTodo(todoId);
        todo.validateMember(loginMember.id());
        return todo;
    }

    private Todo getTodo(long todoId) {
        return todoRepository.findById(todoId)
                .orElseThrow(() -> new EntityNotFoundException("해당 Todo를 찾을 수 없습니다."));
    }

    private Task getTaskWithValidation(long taskId) {
        var loginMember = getLoginMember();
        var task = getTask(taskId);
        task.validateMember(loginMember.id());
        return task;
    }

    private Task getTask(long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("해당 Task를 찾을 수 없습니다."));
    }

    private boolean isAllTasksCompleted(Todo todo) {
        return !taskRepository.existsByTodoAndStatus(todo, Status.INCOMPLETE);
    }

    private boolean isCompleted(Todo todo) {
        return todo.getStatus().isCompleted();
    }
}
