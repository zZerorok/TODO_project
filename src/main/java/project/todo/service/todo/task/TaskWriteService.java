package project.todo.service.todo.task;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.todo.model.todo.Todo;
import project.todo.model.todo.task.Task;
import project.todo.repository.todo.TodoRepository;
import project.todo.repository.todo.task.TaskRepository;
import project.todo.service.todo.task.dto.TaskAddRequest;
import project.todo.service.todo.task.dto.TaskUpdateRequest;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class TaskWriteService {
    private final TaskRepository taskRepository;
    private final TodoRepository todoRepository;

    public void add(Long todoId, TaskAddRequest request) {
        var todo = getTodo(todoId);
        var task = new Task(
                todo,
                request.content()
        );

        taskRepository.save(task);
    }

    public void update(Long taskId, TaskUpdateRequest request) {
        var task = getTask(taskId);
        task.update(request.content());

        taskRepository.save(task);
    }

    public void complete(Long todoId, Long taskId) {
        var task = getTask(taskId);
        task.complete();
        taskRepository.save(task);

        var tasks = findTasksByTodoId(todoId);
        if (isAllTasksCompleted(tasks)) {
            Todo todo = task.getTodo();
            todo.complete();
            todoRepository.save(todo);
        }
    }

    public void delete(Long taskId) {
        var task = getTask(taskId);

        taskRepository.delete(task);
    }

    private Todo getTodo(Long todoId) {
        return todoRepository.findById(todoId)
                .orElseThrow(() -> new EntityNotFoundException("해당 Todo를 찾을 수 없습니다."));
    }

    private Task getTask(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("해당 Task를 찾을 수 없습니다."));
    }

    private List<Task> findTasksByTodoId(Long todoId) {
        var tasks = taskRepository.findAllByTodoId(todoId);

        if (tasks.isEmpty()) {
            throw new EntityNotFoundException("해당 Todo에 대한 Task가 존재하지 않습니다.");
        }

        return tasks;
    }

    private boolean isAllTasksCompleted(List<Task> tasks) {
        return tasks.stream()
                .allMatch(it -> it.getStatus().isCompleted());
    }
}
