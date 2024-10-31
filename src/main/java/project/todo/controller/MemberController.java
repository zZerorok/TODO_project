package project.todo.controller;

import org.springframework.stereotype.Controller;
import project.todo.model.MemberService;

@Controller
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }
}
