package project.todo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import project.todo.model.todo.TodoCreateRequest;
import project.todo.service.TodoService;

@RequestMapping("/todo")
@Controller
public class TodoController {
    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @PostMapping("/create")
    public ResponseEntity<Void> create(@RequestBody TodoCreateRequest request) {
        todoService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/delete/{todoId}")
    public void delete(@PathVariable Long todoId) {
        todoService.delete(todoId);
    }
}
