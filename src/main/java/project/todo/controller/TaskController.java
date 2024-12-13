package project.todo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.todo.service.todo.task.TaskAddRequest;
import project.todo.service.todo.task.TaskResponse;
import project.todo.service.todo.task.TaskUpdateRequest;
import project.todo.service.todo.task.TaskService;

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
        List<TaskResponse> tasks = taskService.findTasks(todoId);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping
    public ResponseEntity<Void> add(@PathVariable Long todoId,
                                    @RequestBody TaskAddRequest request) {
        taskService.add(todoId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{taskId}")
    public ResponseEntity<Void> update(@PathVariable Long taskId,
                                       @RequestBody TaskUpdateRequest request) {
        taskService.update(taskId, request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{taskId}/complete")
    public ResponseEntity<Void> completeTask(@PathVariable Long todoId,
                                             @PathVariable Long taskId) {
        taskService.completeTask(todoId, taskId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> delete(@PathVariable Long taskId) {
        taskService.delete(taskId);
        return ResponseEntity.noContent().build();
    }
}
