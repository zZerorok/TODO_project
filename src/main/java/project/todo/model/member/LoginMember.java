package project.todo.model.member;

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
