package project.todo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.todo.model.todo.Status;
import project.todo.service.todo.task.TaskWriteService;
import project.todo.service.todo.task.dto.TaskAddRequest;
import project.todo.service.todo.task.dto.TaskUpdateRequest;

@RequiredArgsConstructor
@RequestMapping("/todos/{todoId}/tasks")
@RestController
public class TaskController {
    private final TaskWriteService taskWriteService;

    @PostMapping
    public ResponseEntity<Void> add(
            @PathVariable Long todoId,
            @RequestBody TaskAddRequest request
    ) {
        taskWriteService.add(todoId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{taskId}")
    public ResponseEntity<Void> update(
            @PathVariable Long todoId,
            @PathVariable Long taskId,
            @RequestBody TaskUpdateRequest request
    ) {
        taskWriteService.update(todoId, taskId, request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long todoId,
            @PathVariable Long taskId,
            @RequestParam Status status
    ) {
        taskWriteService.updateStatus(todoId, taskId, status);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long todoId,
            @PathVariable Long taskId
    ) {
        taskWriteService.delete(todoId, taskId);
        return ResponseEntity.noContent().build();
    }
}
