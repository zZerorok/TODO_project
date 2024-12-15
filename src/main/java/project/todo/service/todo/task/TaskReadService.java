package project.todo.service.todo.task;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.todo.model.todo.Todo;
import project.todo.model.todo.task.Task;
import project.todo.repository.todo.TodoRepository;
import project.todo.repository.todo.task.TaskRepository;
import project.todo.service.todo.task.dto.TaskDetailResponse;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TaskReadService {
    private final TaskRepository taskRepository;
    private final TodoRepository todoRepository;

    public List<TaskDetailResponse> findTasks(Long todoId) {
        var todo = getTodo(todoId);
        var tasks = getTasks(todo);

        return tasks.stream()
                .map(TaskDetailResponse::from)
                .toList();
    }

    private Todo getTodo(Long todoId) {
        return todoRepository.findById(todoId)
                .orElseThrow(() -> new EntityNotFoundException("해당 Todo를 찾을 수 없습니다."));
    }

    private List<Task> getTasks(Todo todo) {
        var tasks = taskRepository.findAllByTodoId(todo.getId());

        if (tasks.isEmpty()) {
            throw new EntityNotFoundException("해당 Todo에 대한 Task가 존재하지 않습니다.");
        }

        return tasks;
    }
}
