package project.todo.model.todo.task;

public record TaskUpdateRequest(
        String content
) {
    public void updateTo(Task task) {
        validateComplete(task);

        if (content != null) {
            task.changeContent(content);
        }
    }

    private void validateComplete(Task task) {
        if (task.isCompleted()) {
            throw new IllegalArgumentException("이미 완료된 Task는 수정할 수 없습니다.");
        }
    }

    public boolean isChanged(Task task) {
        return !content.equals(task.getContent());
    }
}
