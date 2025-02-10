package project.todo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.todo.model.todo.Status;
import project.todo.service.member.Login;
import project.todo.service.security.dto.LoginMember;
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
            @Login LoginMember loginMember,
            @RequestParam(required = false) Optional<Status> status
    ) {
        var todos = todoReadService.findTodos(loginMember, status);
        return ResponseEntity.ok(todos);
    }

    @GetMapping("/{todoId}")
    public ResponseEntity<TodoWithTasksResponse> getTodoWithTasks(
            @Login LoginMember loginMember,
            @PathVariable Long todoId
    ) {
        var todoWithTasks = todoReadService.getTodoWithTasks(loginMember, todoId);
        return ResponseEntity.ok(todoWithTasks);
    }

    @PostMapping
    public ResponseEntity<Void> create(
            @Login LoginMember loginMember,
            @RequestBody TodoCreateRequest request
    ) {
        todoWriteService.create(loginMember, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{todoId}")
    public ResponseEntity<Void> update(
            @Login LoginMember loginMember,
            @PathVariable Long todoId,
            @RequestBody TodoUpdateRequest request
    ) {
        todoWriteService.update(loginMember, todoId, request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{todoId}")
    public ResponseEntity<Void> updateStatus(
            @Login LoginMember loginMember,
            @PathVariable Long todoId,
            @RequestParam Status status
    ) {
        todoWriteService.updateStatus(loginMember, todoId, status);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{todoId}")
    public ResponseEntity<Void> delete(
            @Login LoginMember loginMember,
            @PathVariable Long todoId
    ) {
        todoWriteService.delete(loginMember, todoId);
        return ResponseEntity.noContent().build();
    }
}
