package project.todo.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.todo.model.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
}
