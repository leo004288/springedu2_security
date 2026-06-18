package com.example.springedu2.controller;

import com.example.springedu2.VisitorRepository;
import com.example.springedu2.entity.Visitor;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class VisitorController {

    // 1.  @Autowired 이용 생성자 주입
    // @Autowired
    // private VisitorRepository visitorRepository;

    // 2. 생성자 주입(최근 방식)
    // private VisitorRepository visitorRepository;
    // public VisitorController(VisitorRepository visitorRepository) {
    //     this.visitorRepository = visitorRepository;
    // }

    // 3. 생성자 주입 다른방법
    // @RequiredArgsConstructor 필수 : lombok 필수
    private final VisitorRepository visitorRepository;

    // 방명록 조회
    @GetMapping("/vlist")
    public ModelAndView vlist() {
        List<Visitor> visitors = visitorRepository.findAll();
        return visitorView(visitors, null);
    }

    private ModelAndView visitorView(List<Visitor> visitors, String buttonText) {
        ModelAndView mv = new ModelAndView("visitorView");
        // mv.setViewName("visitorView");  // visitorView.html(Model 사용) - thymeleaf
        if(visitors.isEmpty()){
            mv.addObject("msg", "조회된 결과가 없습니다");
        } else {
            mv.addObject("vList", visitors);
        }
        if(buttonText != null) {
            mv.addObject("buttonText", buttonText);
        }
        return mv;
    }

    // 방명록 검색
    // 검색 : 모두 대문자로 검색어를 포함한 data
    // 단, 정렬 id를 내림차순으로 출력
    // findByMemoContainingIgnoreCaseOrderByIdDesc(key)
    @GetMapping("/vsearch")
    public ModelAndView vsearch(@RequestParam(defaultValue = "") String key) {
        List<Visitor> visitors = key.isBlank()
                ?   visitorRepository.findAll()
                :   visitorRepository.findByIrum(key);
             // :   visitorRepository.findByMemoContainingIgnoreCaseOrderByIdDesc(key);
             // :   visitorRepository.findByName(key);

        System.out.println(visitors);

        return visitorView(visitors, "메인으로 돌아가기");
    }

    // 방명록 추가
    // @Valid : form 에서 넘어온 자료를 @Entity에 있는
    // 설정(@ID, @NotBlank, @Column(nullable=false))과 비교해 입력
    @PostMapping("/vinsert")
    @Transactional
    public String vinsert(
            @Valid Visitor visitor, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

        System.out.println("visitor:" + visitor);
        System.out.println("bindingResult:" + bindingResult);

        if (bindingResult.hasErrors()){
            model.addAttribute("msg", "이름과 내용을 모두 입력");
            return "visitorView";  // visitorView.html
        }

        visitorRepository.save(visitor);  // entity 객체를 사용해야함

        return "redirect:/vlist";
    }

    // 방명록 id로 조회 : rest방식 호출 결과 : json
    // return 값이 Visitor 객체인데 json으로 변경되어 다운로드된다
    // return  값이 ResponseEntity<Visitor> 일때는 data는 json으로 상태코드로 리턴가능
    @GetMapping(value = "/one", produces = "application/json; charset=utf-8")
    @ResponseBody
    public ResponseEntity<Visitor> one(@RequestParam Integer id) {
        return visitorRepository.findById(Long.valueOf(id))  // data에 id를 조회해 있으면 visitor리턴
                .map(ResponseEntity::ok)  // 상태코드 200을 추가해 리턴
                .orElseGet(()-> ResponseEntity.notFound().build());  // 못찾으면 null 대신에 404 코드를 객체로 바꿔 리턴
    }

    /*
    // 방명록 수정
    @PostMapping("/vupdate")
    public String vupdate(@Valid Visitor visitor,
                          BindingResult bindingResult,
                          Model model, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("msg", "수정할 이름과 내용을 입력하세요");
            return "redirect:vlist";
        }

        //수정
        visitorRepository.save(visitor);

        return "redirect:vlist";
    }
    */

    // 방명록 수정 2
    @PostMapping("/vupdate")
    @Transactional
    public String vupdate(@Valid Visitor visitor,
                          BindingResult bindingResult,
                          Model model, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("msg", "수정할 이름과 내용을 입력하세요");
            return "redirect:vlist";
        }

        Visitor entity = visitorRepository.findById(Long.valueOf(visitor.getId()))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방명록입니다"));
        entity.setName(visitor.getName());
        entity.setMemo(visitor.getMemo());
        return "redirect:vlist";
    }

    // 방명록 삭제
    @PostMapping("vdelete")
    @Transactional
    public String vdelete(@RequestParam Integer id,
                          RedirectAttributes redirectAttributes) {
        if(!visitorRepository.existsById(Long.valueOf(id))) {
            redirectAttributes.addFlashAttribute("msg", "삭제할 방명록을 찿을 수 없습니다.");
            return "redirect:/vlist";
        }

        visitorRepository.deleteById(Long.valueOf(id));
        return "redirect:/vlist";
    }

}
