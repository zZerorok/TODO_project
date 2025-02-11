package project.todo.service.todo.task;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.todo.exception.todo.TodoNotFoundException;
import project.todo.exception.todo.task.TaskNotFoundException;
import project.todo.model.todo.Status;
import project.todo.model.todo.Todo;
import project.todo.model.todo.task.Task;
import project.todo.repository.todo.TodoRepository;
import project.todo.repository.todo.task.TaskRepository;
import project.todo.service.security.dto.LoginMember;
import project.todo.service.todo.task.dto.TaskAddRequest;
import project.todo.service.todo.task.dto.TaskUpdateRequest;

/**
 * Task 쓰기 작업 요청을 처리하는 서비스 클래스
 */
@RequiredArgsConstructor
@Transactional
@Service
public class TaskWriteService {
    private final TaskRepository taskRepository;
    private final TodoRepository todoRepository;

    /**
     * 특정 Todo에 새로운 Task를 추가합니다.
     *
     * @param todoId Task를 추가할 Todo의 ID
     * @param request Task 추가 요청 객체
     */
    public void add(LoginMember loginMember, Long todoId, TaskAddRequest request) {
        var todo = getTodoWithValidation(loginMember, todoId);
        var task = new Task(todo, request.content());

        taskRepository.save(task);
    }

    /**
     * 특정 Todo에 포함된 Task를 수정합니다.
     *
     * @param todoId 해당 Task가 속한 Todo의 ID
     * @param taskId 수정할 Task의 ID
     * @param request Task 수정 요청 객체
     */
    public void update(LoginMember loginMember, Long todoId, Long taskId, TaskUpdateRequest request) {
        var task = getTaskWithValidation(loginMember, todoId, taskId);

        task.update(request.content());
    }

    /**
     * 요청 상태에 따라 Task의 완료/미완료 처리를 진행합니다.<p>
     *
     * - Task를 완료한 경우, 해당 Task가 속한 Todo의 모든 Task가 완료되면 Todo도 자동 완료 처리됩니다.<p>
     * - Task를 미완료 처리한 경우, Todo가 이미 완료 상태라면 Todo도 미완료 처리됩니다.<p>
     *
     * @param todoId 해당 Task가 속한 Todo의 ID
     * @param taskId 완료/미완료 처리를 진행할 Task의 ID
     * @param status Task의 상태 (완료 또는 미완료)
     */
    public void updateStatus(LoginMember loginMember, Long todoId, Long taskId, Status status) {
        if (status == Status.COMPLETE) {
            complete(loginMember, todoId, taskId);
        }

        if (status == Status.INCOMPLETE) {
            incomplete(loginMember, todoId, taskId);
        }
    }

    /**
     * 특정 Task를 삭제합니다.
     *
     * @param todoId 해당 Task가 속한 Todo의 ID
     * @param taskId 삭제할 Task의 ID
     */
    public void delete(LoginMember loginMember, Long todoId, Long taskId) {
        var task = getTaskWithValidation(loginMember, todoId, taskId);
        taskRepository.delete(task);

        var todo = getTodo(todoId);
        if (isLastTask(todo) && isCompleted(todo)) {
            todo.incomplete();
        }
    }

    private Todo getTodoWithValidation(LoginMember loginMember, long todoId) {
        var todo = getTodo(todoId);
        todo.validateWriter(loginMember.id());
        return todo;
    }

    private Todo getTodo(long todoId) {
        return todoRepository.findById(todoId)
                .orElseThrow(() -> new TodoNotFoundException("해당 Todo를 찾을 수 없습니다."));
    }

    private Task getTaskWithValidation(LoginMember loginMember, long todoId, long taskId) {
        var task = getTask(taskId);
        task.validateWriter(loginMember.id());
        task.validateTodo(todoId);
        return task;
    }

    private Task getTask(long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("해당 Task를 찾을 수 없습니다."));
    }

    private void complete(LoginMember loginMember, Long todoId, Long taskId) {
        var task = getTaskWithValidation(loginMember, todoId, taskId);
        task.complete();

        var todo = getTodoWithValidation(loginMember, todoId);
        if (isAllTasksCompleted(todo)) {
            todo.complete();
        }
    }

    private void incomplete(LoginMember loginMember, Long todoId, Long taskId) {
        var task = getTaskWithValidation(loginMember, todoId, taskId);
        task.incomplete();

        var todo = getTodoWithValidation(loginMember, todoId);
        if (isCompleted(todo)) {
            todo.incomplete();
        }
    }

    private boolean isAllTasksCompleted(Todo todo) {
        return !taskRepository.existsByTodoAndStatus(todo, Status.INCOMPLETE);
    }

    private boolean isCompleted(Todo todo) {
        return todo.getStatus().isCompleted();
    }

    private boolean isLastTask(Todo todo) {
        return !taskRepository.existsByTodo(todo);
    }
}
