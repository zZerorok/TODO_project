package project.todo.exception.todo.task;

public class TaskNotContainsInTodoException extends RuntimeException {

    public TaskNotContainsInTodoException(Long todoId) {
        super("해당 Task는 " + todoId + "에 포함되어 있지 않습니다.");
    }
}
