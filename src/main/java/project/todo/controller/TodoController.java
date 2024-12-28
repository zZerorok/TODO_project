package project.todo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.todo.service.todo.TodoReadService;
import project.todo.service.todo.TodoWriteService;
import project.todo.service.todo.dto.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/todos")
@RestController
public class TodoController {
    private final TodoReadService todoReadService;
    private final TodoWriteService todoWriteService;

    @GetMapping("/{memberId}")
    public ResponseEntity<List<TodoSimpleResponse>> findTodos(@PathVariable Long memberId) {
        var todos = todoReadService.findTodos(memberId);
        return ResponseEntity.ok(todos);
    }

    @GetMapping("/{memberId}/complete")
    public ResponseEntity<List<TodoDetailResponse>> findCompleteTodos(@PathVariable Long memberId) {
        var completeTodos = todoReadService.findCompleteTodos(memberId);
        return ResponseEntity.ok(completeTodos);
    }

    @GetMapping("/{memberId}/incomplete")
    public ResponseEntity<List<TodoDetailResponse>> findIncompleteTodos(@PathVariable Long memberId) {
        var incompleteTodos = todoReadService.findIncompleteTodos(memberId);
        return ResponseEntity.ok(incompleteTodos);
    }

    @GetMapping("/{todoId}")
    public ResponseEntity<TodoWithTasksResponse> getTodoWithTasks(@PathVariable Long todoId) {
        var todoWithTasks = todoReadService.getTodoWithTasks(todoId);
        return ResponseEntity.ok(todoWithTasks);
    }

    @PostMapping("/{memberId}")
    public ResponseEntity<Void> create(@PathVariable Long memberId,
                                       @RequestBody TodoCreateRequest request) {
        todoWriteService.create(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{todoId}")
    public ResponseEntity<Void> update(@PathVariable Long todoId,
                                       @RequestBody TodoUpdateRequest request) {
        todoWriteService.update(todoId, request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{todoId}/complete")
    public ResponseEntity<Void> complete(@PathVariable Long todoId) {
        todoWriteService.complete(todoId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{todoId}/incomplete")
    public ResponseEntity<Void> incomplete(@PathVariable Long todoId) {
        todoWriteService.incomplete(todoId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{todoId}")
    public ResponseEntity<Void> delete(@PathVariable Long todoId) {
        todoWriteService.delete(todoId);
        return ResponseEntity.noContent().build();
    }
}
