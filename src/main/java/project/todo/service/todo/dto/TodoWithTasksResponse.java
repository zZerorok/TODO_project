package project.todo.service.todo.dto;

import project.todo.model.todo.Todo;
import project.todo.model.todo.TodoStatus;
import project.todo.model.todo.task.Task;
import project.todo.model.todo.task.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record TodoWithTasksResponse(
        Long todoId,
        String title,
        LocalDate deadline,
        LocalDateTime createdAt,
        TodoStatus status,
        List<TaskSimpleResponse> tasks
) {

    public static TodoWithTasksResponse from(Todo todo, List<Task> tasks) {
        var taskResponses = tasks.stream()
                .map(TaskSimpleResponse::from)
                .toList();

        return new TodoWithTasksResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getDeadline().toLocalDate(),
                todo.getCreatedAt(),
                todo.getStatus(),
                taskResponses
        );
    }

    private record TaskSimpleResponse(
            Long taskId,
            String content,
            TaskStatus status
    ) {

        private static TaskSimpleResponse from(Task task) {
            return new TaskSimpleResponse(
                    task.getId(),
                    task.getContent(),
                    task.getStatus()
            );
        }
    }
}
