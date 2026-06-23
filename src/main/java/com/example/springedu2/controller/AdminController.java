package com.example.springedu2.controller;

import com.example.springedu2.dto.MemberCreateForm;
import com.example.springedu2.entity.Member;
import com.example.springedu2.service.memberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final memberService memberService;
/*
    public AdminController(memberService memberService) {
        this.memberService = memberService;
    }
*/

    // 회원목록
    @GetMapping("/admin/members")
    public String memberList(Model model) {
        List<Member> memberList = memberService.findAll();
        model.addAttribute("memberList", memberList);
        return "memberList";  // memberList.html
    }

    // 회원추가(관리자)
    @PostMapping("/admin/members")
    public String adminCreate(
            @Valid @ModelAttribute("memberForm") MemberCreateForm memberCreateForm,
            BindingResult bindingResult) throws IllegalAccessException {

        if (bindingResult.hasErrors()) {
            return "/memberAdminForm"; //  다시 입력받기
        }

        // 새 회원을 추가 (관리자)
        try {
            memberService.create(memberCreateForm);
        } catch (IllegalArgumentException e) {
            bindingResult.reject("createFail", e.getMessage());
            return "memberAdmiForm";      // 회원추가실패 -> 다시 추가화면으로 이동
        }

        return "redirect:/admin/members"; // 목록조회
    }

    // 회원추가
    @GetMapping("/admin/members/new")
    public String adminCreateForm(Model model) {
        model.addAttribute("memberForm", new MemberCreateForm());
        return "memberAdminForm";
    }

}
