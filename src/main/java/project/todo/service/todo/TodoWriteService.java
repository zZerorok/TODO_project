package project.todo.service.todo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.todo.exception.todo.TodoNotFoundException;
import project.todo.model.todo.Status;
import project.todo.model.todo.Todo;
import project.todo.repository.todo.TodoRepository;
import project.todo.repository.todo.task.TaskRepository;
import project.todo.service.security.dto.LoginMember;
import project.todo.service.todo.dto.TodoCreateRequest;
import project.todo.service.todo.dto.TodoUpdateRequest;

/**
 * Todo 쓰기 작업 요청을 처리하는 서비스 클래스
 */
@RequiredArgsConstructor
@Transactional
@Service
public class TodoWriteService {
    private final TodoRepository todoRepository;
    private final TaskRepository taskRepository;

    /**
     * 새로운 Todo를 생성합니다.
     *
     * @param request Todo 생성 요청 객체
     */
    public void create(LoginMember loginMember, TodoCreateRequest request) {
        var todo = new Todo(
                loginMember.id(),
                request.title(),
                request.deadLine()
        );

        todoRepository.save(todo);
    }

    /**
     * 특정 Todo를 수정합니다.
     *
     * @param todoId 수정할 Todo의 ID
     * @param request Todo 수정 요청 객체
     */
    public void update(LoginMember loginMember, Long todoId, TodoUpdateRequest request) {
        var todo = getTodoWithValidation(loginMember, todoId);

        todo.update(request.title(), request.deadline());
    }

    /**
     * 요청 상태에 따라 Todo의 완료/미완료 처리를 진행합니다.<p>
     *
     * - Todo를 완료 처리하려면, 해당 Todo에 포함된 모든 Task가 완료 상태여야 합니다.<p>
     * - Todo를 미완료 처리하려면, Todo가 완료 상태여야 하며 확인 후 상태를 변경합니다.<p>
     *
     * @param todoId
     * @param status 변경할 요청의 상태(완료 또는 미완료)
     */
    public void updateStatus(LoginMember loginMember, Long todoId, Status status) {
        if (status == Status.COMPLETE) {
            complete(loginMember, todoId);
        }

        if (status == Status.INCOMPLETE) {
            incomplete(loginMember, todoId);
        }
    }

    /**
     * 특정 Todo를 삭제합니다.
     *
     * @param todoId 삭제할 Todo의 ID
     */
    public void delete(LoginMember loginMember, Long todoId) {
        var todo = getTodoWithValidation(loginMember, todoId);

        deleteTodoAndTasks(todo);
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

    private void complete(LoginMember loginMember, Long todoId) {
        var todo = getTodoWithValidation(loginMember, todoId);

        if (isAllTasksCompleted(todo)) {
            todo.complete();
        }
    }

    private void incomplete(LoginMember loginMember, Long todoId) {
        var todo = getTodoWithValidation(loginMember, todoId);

        todo.incomplete();
    }

    private boolean isAllTasksCompleted(Todo todo) {
        return !taskRepository.existsByTodoAndStatus(todo, Status.INCOMPLETE);
    }

    private void deleteTodoAndTasks(Todo todo) {
        deleteTasksFromTodo(todo);
        deleteTodo(todo);
    }

    private void deleteTasksFromTodo(Todo todo) {
        taskRepository.deleteAllByTodoId(todo.getId());
    }

    private void deleteTodo(Todo todo) {
        todoRepository.delete(todo);
    }
}
