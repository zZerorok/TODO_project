package project.todo.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import project.todo.model.todo.Todo;
import project.todo.model.todo.TodoRepository;
import project.todo.model.todo.task.Task;
import project.todo.model.todo.task.TaskAddRequest;
import project.todo.model.todo.task.TaskRepository;
import project.todo.model.todo.task.TaskResponse;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final TodoRepository todoRepository;

    public TaskService(TaskRepository taskRepository, TodoRepository todoRepository) {
        this.taskRepository = taskRepository;
        this.todoRepository = todoRepository;
    }

    public List<TaskResponse> findTasks(Long todoId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new EntityNotFoundException("해당 Todo를 찾을 수 없습니다."));

        List<Task> tasks = taskRepository.findAllByTodoId(todo.getId());

        if (tasks.isEmpty()) {
            throw new EntityNotFoundException("해당 Todo에 대한 Task가 존재하지 않습니다.");
        }

        return tasks.stream()
                .map(TaskResponse::from)
                .toList();
    }

    public void add(Long todoId, TaskAddRequest request) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new EntityNotFoundException("해당 Todo를 찾을 수 없습니다."));

        Task task = new Task(
                todo,
                request.content()
        );

        taskRepository.save(task);
    }

    public void completeTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("해당 Task를 찾을 수 없습니다."));

        if (task.isCompleted()) {
            throw new IllegalStateException("이미 완료된 Task 입니다.");
        }

        task.completeTask();

        taskRepository.save(task);
    }

    public void delete(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("해당 Task를 찾을 수 없습니다."));

        taskRepository.delete(task);
    }
}
