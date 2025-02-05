package project.todo.service.security;

import project.todo.service.security.dto.LoginMember;

/**
 * 사용자 세션을 관리하는 컴포넌트
 */
public interface SessionHolder {

    /**
     * @return 로그인된 멤버 객체
     */
    LoginMember getSession();

    /**
     * @param loginMember 로그인 시킬 멤버 객체
     */
    void setSession(LoginMember loginMember);

    /**
     * 세션 삭제
     */
    void removeSession();
}
