package com.example.springedu2;

import com.example.springedu2.dto.MemberCreateForm;
import com.example.springedu2.entity.Member;
import com.example.springedu2.entity.Role;
import com.example.springedu2.repository.MemberRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class memberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 로그인을 위해 db에서 회원정보를 조회해서 UserDetails를 생성
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 회원정보 db에서 회원이름으로 조회
        Member member = memberRepository.findByUsername(username)
                .orElseThrow( () -> new UsernameNotFoundException(
                        "존재하지 않는 사용자입니다"
                ));
        // 조회한 결과를 Member -> UserDetails 로 변환
        UserDetails user = User.builder()
                .username(member.getUsername())     // 아이디
                .password(member.getPassword())     // 비번
                .disabled(!member.isEnabled())      // 계정 사용가능
                .roles(member.getRole().toString()) // 사용자 권한 "ADMIN" -> Role_ADMIN 권한
                .build();
        return user;
    }

    // ---------------------------------------------------------------------------------------
    // 일반유저 회원가입
    public Member register(@Valid MemberCreateForm memberForm) {
        memberForm.setRole(Role.USER.name() );
        return create(memberForm);
    }

    // 회원가입
    @Transactional
    private Member create(@Valid MemberCreateForm memberForm) {
        // 기존회원인지 조회

        Member member = new Member();

        member.setUsername( memberForm.getUsername() );
        member.setPassword( passwordEncoder.encode( memberForm.getPassword() ) );
        member.setName( memberForm.getName() );
        member.setEmail( memberForm.getEmail() );
        member.setRole( parseRole( memberForm.getRole() ) );
        member.setEnabled(true);

        return memberRepository.save(member);
    }

    // 권한 문자열 변환 "ADMIN" -> Role.ADMIN
    private Role parseRole(String role) {
        if( role == null || role.isBlank() ) {
            return Role.USER;
        }
        return Role.valueOf( role.toUpperCase() );
    }

}
