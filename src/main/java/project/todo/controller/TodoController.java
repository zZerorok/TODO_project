package project.todo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.todo.service.todo.TodoWriteService;
import project.todo.service.todo.dto.*;
import project.todo.service.todo.TodoReadService;

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

    @DeleteMapping("/{todoId}")
    public ResponseEntity<Void> delete(@PathVariable Long todoId) {
        todoWriteService.delete(todoId);
        return ResponseEntity.noContent().build();
    }
}
