package project.todo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.todo.model.todo.TodoCreateRequest;
import project.todo.model.todo.TodoResponse;
import project.todo.model.todo.TodoUpdateRequest;
import project.todo.service.TodoService;

import java.util.List;

@RequestMapping("/todos")
@RestController
public class TodoController {
    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<List<TodoResponse>> findTodos(@PathVariable Long memberId) {
        List<TodoResponse> todos = todoService.findTodos(memberId);
        return ResponseEntity.ok(todos);
    }

    @PostMapping("/{memberId}")
    public ResponseEntity<Void> create(@PathVariable Long memberId,
                                       @RequestBody TodoCreateRequest request) {
        todoService.create(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{todoId}")
    public ResponseEntity<Void> update(@PathVariable Long todoId,
                                       @RequestBody TodoUpdateRequest request) {
        todoService.update(todoId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{todoId}")
    public ResponseEntity<Void> delete(@PathVariable Long todoId) {
        todoService.delete(todoId);
        return ResponseEntity.noContent().build();
    }
}
