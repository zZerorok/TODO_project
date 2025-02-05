package project.todo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import project.todo.service.member.LoginService;
import project.todo.service.member.MemberService;
import project.todo.service.member.dto.MemberCreateRequest;
import project.todo.service.member.dto.MemberLoginRequest;
import project.todo.service.security.SessionHolder;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/members")
@Controller
public class MemberController {
    private final MemberService memberService;
    private final LoginService loginService;
    private final SessionHolder sessionHolder;

    @GetMapping("/home")
    public String hello(Model model) {
        var member = sessionHolder.getSession();
        if (member == null) {
            return "redirect:/members/login-form";
        }

        model.addAttribute("loginMember", member);
        return "members/home";
    }

    @GetMapping("/register-form")
    public String registerForm(Model model) {
        model.addAttribute("form", MemberCreateRequest.EMPTY);
        return "members/register-form";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") MemberCreateRequest request,
                           BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "members/register-form";
        }

        memberService.register(request);
        return "redirect:/";
    }

    @GetMapping("/login-form")
    public String loginForm(Model model) {
        model.addAttribute("loginForm", MemberLoginRequest.EMPTY);
        return "members/login-form";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginForm") MemberLoginRequest loginRequest,
                        BindingResult bindingResult,
                        Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "members/login-form";
        }

        var loginMember = loginService.login(loginRequest);
        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "members/login-form";
        }

        sessionHolder.setSession(loginMember);
        model.addAttribute("loginMember", loginMember);
        return "members/home";
    }

    @GetMapping("/logout")
    public String logout() {
        sessionHolder.removeSession();
        return "redirect:/";
    }
}
