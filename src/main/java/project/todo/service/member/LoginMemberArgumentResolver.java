package project.todo.service.member;

import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import project.todo.service.security.SessionHolder;
import project.todo.service.security.dto.LoginMember;

@RequiredArgsConstructor
@Component
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {
    private final SessionHolder sessionHolder;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasLoginAnnotation = parameter.hasParameterAnnotation(Login.class);
        boolean hasMemberType = LoginMember.class.isAssignableFrom(parameter.getParameterType());

        return hasLoginAnnotation && hasMemberType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        return sessionHolder.getSession();
    }
}
