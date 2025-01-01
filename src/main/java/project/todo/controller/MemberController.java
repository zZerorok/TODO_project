package project.todo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import project.todo.model.member.Member;
import project.todo.service.member.MemberCreateRequest;
import project.todo.service.member.MemberService;

@RequestMapping("/members")
@Controller
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/hello")
    public String hello(@RequestParam String name, Model model) {
        model.addAttribute("name", name);
        return "hello";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute MemberCreateRequest request) {
        Member savedMember = memberService.save(request);
        return "redirect:/member/hello?name=" + savedMember.getName();
    }

    @GetMapping("/register-form")
    public String registerForm() {
        return "members/register-form";
    }

    @GetMapping("/login-form")
    public String loginForm() {
        return "members/login-form";
    }
}
