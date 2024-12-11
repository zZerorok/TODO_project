package project.todo.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import project.todo.model.member.Member;
import project.todo.model.member.MemberRepository;
import project.todo.model.todo.*;

import java.util.List;

@Service
public class TodoService {
    private final TodoRepository todoRepository;
    private final MemberRepository memberRepository;

    public TodoService(TodoRepository todoRepository, MemberRepository memberRepository) {
        this.todoRepository = todoRepository;
        this.memberRepository = memberRepository;
    }

    public List<TodoResponse> findTodos(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        List<Todo> todos = todoRepository.findAllByMemberId(member.getId());

        if (todos.isEmpty()) {
            throw new EntityNotFoundException("작성하신 Todo가 없습니다.");
        }

        return todos.stream()
                .map(TodoResponse::from)
                .toList();
    }

    public void create(Long memberId, TodoCreateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        Todo todo = new Todo(
                member,
                request.title(),
                request.deadLine()
        );

        todoRepository.save(todo);
    }

    public void update(Long todoId, TodoUpdateRequest request) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new EntityNotFoundException("Todo not found with id: " + todoId));

        request.updateTo(todo);

        if (request.isChanged(todo)) {
            todoRepository.save(todo);
        }
    }

    public void delete(Long todoId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new EntityNotFoundException("Todo not found with id: " + todoId));

        todoRepository.delete(todo);
    }
}
