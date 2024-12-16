package project.todo.service.todo;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.todo.model.member.Member;
import project.todo.model.todo.Todo;
import project.todo.repository.member.MemberRepository;
import project.todo.repository.todo.TodoRepository;
import project.todo.repository.todo.task.TaskRepository;
import project.todo.service.todo.dto.TodoCreateRequest;
import project.todo.service.todo.dto.TodoUpdateRequest;

@RequiredArgsConstructor
@Transactional
@Service
public class TodoWriteService {
    private final TaskRepository taskRepository;
    private final MemberRepository memberRepository;
    private final TodoRepository todoRepository;

    public void create(Long memberId, TodoCreateRequest request) {
        var member = getMember(memberId);
        var todo = new Todo(
                member.getId(),
                request.title(),
                request.deadLine()
        );

        todoRepository.save(todo);
    }

    public void update(Long todoId, TodoUpdateRequest request) {
        var todo = getTodo(todoId);
        todo.update(
                request.title(),
                request.deadline()
        );
    }

    public void delete(Long todoId) {
        var todo = getTodo(todoId);

        taskRepository.deleteByTodoId(todo.getId());
        todoRepository.delete(todo);
    }

    private Member getMember(long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
    }

    private Todo getTodo(long todoId) {
        return todoRepository.findById(todoId)
                .orElseThrow(() -> new EntityNotFoundException("해당 Todo가 존재하지 않습니다."));
    }
}
