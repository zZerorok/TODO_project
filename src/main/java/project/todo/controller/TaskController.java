package project.todo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.todo.service.todo.task.TaskWriteService;
import project.todo.service.todo.task.dto.TaskAddRequest;
import project.todo.service.todo.task.dto.TaskDetailResponse;
import project.todo.service.todo.task.dto.TaskUpdateRequest;
import project.todo.service.todo.task.TaskReadService;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/todos/{todoId}/tasks")
@RestController
public class TaskController {
    private final TaskReadService taskReadService;
    private final TaskWriteService taskWriteService;

    @GetMapping
    public ResponseEntity<List<TaskDetailResponse>> findTasks(@PathVariable Long todoId) {
        var tasks = taskReadService.findTasks(todoId);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping
    public ResponseEntity<Void> add(@PathVariable Long todoId,
                                    @RequestBody TaskAddRequest request) {
        taskWriteService.add(todoId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{taskId}")
    public ResponseEntity<Void> update(@PathVariable Long taskId,
                                       @RequestBody TaskUpdateRequest request) {
        taskWriteService.update(taskId, request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{taskId}/complete")
    public ResponseEntity<Void> complete(@PathVariable Long todoId,
                                         @PathVariable Long taskId) {
        taskWriteService.complete(todoId, taskId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{taskId}/incomplete")
    public ResponseEntity<Void> incomplete(@PathVariable Long todoId,
                                           @PathVariable Long taskId) {
        taskWriteService.incomplete(todoId, taskId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> delete(@PathVariable Long taskId) {
        taskWriteService.delete(taskId);
        return ResponseEntity.noContent().build();
    }
}
