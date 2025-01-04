package project.todo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import project.todo.model.member.Member;
import project.todo.service.member.MemberCreateRequest;
import project.todo.service.member.MemberLoginRequest;
import project.todo.service.member.MemberService;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/members")
@Controller
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/hello")
    public String hello(@ModelAttribute Member loginMember,
                        Model model) {
        model.addAttribute("loginMember", loginMember);
        return "hello";
    }

    @GetMapping("/register-form")
    public String registerForm(Model model) {
        var registerForm = new MemberCreateRequest(
                null,
                null,
                null,
                null
        );
        model.addAttribute("form", registerForm);
        return "members/register-form";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") MemberCreateRequest request,
                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "members/register-form";
        }

        memberService.register(request);
        return "redirect:/";
    }

    @GetMapping("/login-form")
    public String loginForm(Model model) {
        var loginForm = new MemberLoginRequest(
                null,
                null
        );
        model.addAttribute("loginForm", loginForm);
        return "members/login-form";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginForm") MemberLoginRequest request,
                        BindingResult bindingResult,
                        Model model) {
        if (bindingResult.hasErrors()) {
            return "members/login-form";
        }

        var loginMember = memberService.login(request);

        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "members/login-form";
        }

        model.addAttribute("loginMember", loginMember);
        return "hello";
    }
}
