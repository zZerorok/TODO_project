package project.todo.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;
import project.todo.model.member.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);
}
