package project.todo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.todo.model.todo.task.TaskAddRequest;
import project.todo.model.todo.task.TaskResponse;
import project.todo.service.TaskService;

import java.util.List;

@RequestMapping("/todos/{todoId}/tasks")
@RestController
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> findTasks(@PathVariable Long todoId) {
        return ResponseEntity.ok(taskService.findTasks(todoId));
    }

    @PostMapping
    public ResponseEntity<Void> add(@PathVariable Long todoId,
                                    @RequestBody TaskAddRequest request) {
        taskService.add(todoId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{taskId}/complete")
    public ResponseEntity<Void> completeTask(@PathVariable Long todoId,
                                             @PathVariable Long taskId) {
        taskService.completeTask(todoId, taskId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> delete(@PathVariable Long taskId) {
        taskService.delete(taskId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
