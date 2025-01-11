package project.todo.service.member;

import jakarta.validation.constraints.NotEmpty;

public record MemberLoginRequest(
        @NotEmpty(message = "아이디를 입력해주세요.")
        String loginId,

        @NotEmpty(message = "비밀번호를 입력해주세요.")
        String password
) {
    public static final MemberLoginRequest EMPTY = new MemberLoginRequest(
            null,
            null
    );
}
