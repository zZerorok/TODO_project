package project.todo.service.security.dto;

import project.todo.model.member.Member;

public record LoginMember(
        Long id,
        String name
) {

    public static LoginMember from(Member member) {
        return new LoginMember(
                member.getId(),
                member.getName()
        );
    }
}
