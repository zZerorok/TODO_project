package project.todo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.todo.model.todo.Status;
import project.todo.service.todo.TodoReadService;
import project.todo.service.todo.TodoWriteService;
import project.todo.service.todo.dto.TodoCreateRequest;
import project.todo.service.todo.dto.TodoResponse;
import project.todo.service.todo.dto.TodoUpdateRequest;
import project.todo.service.todo.dto.TodoWithTasksResponse;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RequestMapping("/todos")
@RestController
public class TodoController {
    private final TodoReadService todoReadService;
    private final TodoWriteService todoWriteService;

    @GetMapping
    public ResponseEntity<List<TodoResponse>> findTodos(
            @RequestParam(required = false) Optional<Status> status
    ) {
        var todos = todoReadService.findTodos(status);
        return ResponseEntity.ok(todos);
    }

    @GetMapping("/{todoId}")
    public ResponseEntity<TodoWithTasksResponse> getTodoWithTasks(
            @PathVariable Long todoId
    ) {
        var todoWithTasks = todoReadService.getTodoWithTasks(todoId);
        return ResponseEntity.ok(todoWithTasks);
    }

    @PostMapping
    public ResponseEntity<Void> create(
            @RequestBody TodoCreateRequest request
    ) {
        todoWriteService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{todoId}")
    public ResponseEntity<Void> update(
            @PathVariable Long todoId,
            @RequestBody TodoUpdateRequest request
    ) {
        todoWriteService.update(todoId, request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{todoId}/complete")
    public ResponseEntity<Void> complete(
            @PathVariable Long todoId
    ) {
        todoWriteService.complete(todoId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{todoId}/incomplete")
    public ResponseEntity<Void> incomplete(
            @PathVariable Long todoId
    ) {
        todoWriteService.incomplete(todoId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{todoId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long todoId
    ) {
        todoWriteService.delete(todoId);
        return ResponseEntity.noContent().build();
    }
}
