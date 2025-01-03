package project.todo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import project.todo.service.member.MemberCreateRequest;
import project.todo.service.member.MemberService;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/members")
@Controller
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/hello")
    public String hello(@RequestParam String name, Model model) {
        model.addAttribute("name", name);
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
    public String loginForm() {
        return "members/login-form";
    }
}
