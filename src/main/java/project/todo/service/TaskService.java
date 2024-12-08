package project.todo.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import project.todo.model.todo.Todo;
import project.todo.model.todo.TodoRepository;
import project.todo.model.todo.task.*;

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

    public void update(Long taskId, TaskUpdateRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("해당 Task를 찾을 수 없습니다."));

        task.updateFrom(request);

        taskRepository.save(task);
    }

    public void completeTask(Long todoId, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("해당 Task를 찾을 수 없습니다."));

        task.complete();
        taskRepository.save(task);

        List<Task> tasks = taskRepository.findAllByTodoId(todoId);

        if (tasks.isEmpty()) {
            throw new EntityNotFoundException("해당 Todo에 대한 Task가 존재하지 않습니다.");
        }

        boolean isAllTasksCompleted = tasks.stream()
                .allMatch(Task::isCompleted);

        if (isAllTasksCompleted) {
            Todo todo = task.getTodo();
            todo.complete();
            todoRepository.save(todo);
        }
    }

    public void delete(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("해당 Task를 찾을 수 없습니다."));

        taskRepository.delete(task);
    }
}
