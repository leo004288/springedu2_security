package com.example.springedu2.controller;

import com.example.springedu2.dto.MemberCreateForm;
import com.example.springedu2.memberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private memberService memberService;
    /*
    public MemberController(MemberService memberService) {
        this.memberService = memberService
    }
    */

    // 회원가입페이지 이동
    @GetMapping("/member/reigster")
    public String reigsterForm(Model model) {
        model.addAttribute("member", new MemberCreateForm());
        return "memberReigster";  // memberReigster.html
    }

    // 회원가입
    @PostMapping("@{members/register}")
    public String registerMember(
            @Valid @ModelAttribute("memberForm") MemberCreateForm memberForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        // 입력에 오류가 있다면 다시 입력화면으로 돌아가기
        if(bindingResult.hasErrors()) {
            return "memberReigster";  // memberReigster.html
        }

        // 회원가입 : DB에 저장
        memberService.register(memberForm);
        redirectAttributes.addFlashAttribute("msg", "회원가입이 완료되었습니다. 로그인하세요");
        return "redirect:/login";
    }

}
