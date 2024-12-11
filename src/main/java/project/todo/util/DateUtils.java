package project.todo.util;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DateUtils {

    private DateUtils() {
        throw new IllegalStateException("인스턴스를 만들 수 없습니다.");
    }

    public static LocalDateTime toEndOfDay(LocalDate date) {
        return date.atTime(23, 59, 59);
    }
}
