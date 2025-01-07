package project.todo.service.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@Component
public class ServletSessionHolder implements SessionHolder {
    private static final String LOGIN_MEMBER_KEY = "LOGIN_MEMBER_KEY";

    @Override
    public LoginMember getSession() {
        var servletRequest = getCurrentRequest();
        return (LoginMember) servletRequest.getSession()
                .getAttribute(LOGIN_MEMBER_KEY);
    }

    @Override
    public void setSession(LoginMember loginMember) {
        var servletRequest = getCurrentRequest();
        servletRequest.getSession()
                .setAttribute(LOGIN_MEMBER_KEY, loginMember);
    }

    @Override
    public void removeSession() {
        var servletRequest = getCurrentRequest();
        servletRequest.getSession()
                .removeAttribute(LOGIN_MEMBER_KEY);
    }

    public HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(
                RequestContextHolder.getRequestAttributes()))
                .getRequest();
    }
}
