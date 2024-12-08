package project.todo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import project.todo.model.todo.TodoCreateRequest;
import project.todo.model.todo.TodoResponse;
import project.todo.model.todo.TodoUpdateRequest;
import project.todo.service.TodoService;

import java.util.List;

@RequestMapping("/todos")
@Controller
public class TodoController {
    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping("/{memberId}")
    public List<TodoResponse> findAll(@PathVariable Long memberId) {
        return todoService.findAll(memberId);
    }

    @PostMapping("/create")
    public ResponseEntity<Void> create(@RequestBody TodoCreateRequest request) {
        todoService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{todoId}")
    public ResponseEntity<Void> updateAll(@PathVariable Long todoId,
                                          @RequestBody TodoUpdateRequest request) {
        todoService.updateAll(todoId, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/{todoId}")
    public ResponseEntity<Void> update(@PathVariable Long todoId,
                                       @RequestBody TodoUpdateRequest request) {
        todoService.update(todoId, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @DeleteMapping("/{todoId}/delete")
    public void delete(@PathVariable Long todoId) {
        todoService.delete(todoId);
    }
}
