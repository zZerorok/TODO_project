package project.todo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import project.todo.model.member.Member;
import project.todo.model.member.MemberCreateRequest;
import project.todo.service.MemberService;

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

    @PostMapping("/hello")
    public String save(MemberCreateRequest request) {
        Member savedMember = memberService.save(request);
        return "redirect:/hello?name=" + savedMember.getName();
    }
}
