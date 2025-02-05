package project.todo.service.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record MemberCreateRequest(
        @NotEmpty(message = "이름은 공백일 수 없습니다.")
        String name,

        @NotEmpty(message = "아이디는 공백일 수 없습니다.")
        String loginId,

        @NotEmpty(message = "비밀번호는 공백일 수 없습니다.")
        String password,

        @NotEmpty(message = "이메일은 공백일 수 없습니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email
) {
    public static final MemberCreateRequest EMPTY = new MemberCreateRequest(
            null,
            null,
            null,
            null
    );
}
