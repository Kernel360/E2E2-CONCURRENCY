package org.example.catch_line.member.service;

import org.example.catch_line.common.constant.Role;
import org.example.catch_line.member.model.dto.MemberUpdateRequest;
import org.example.catch_line.member.model.vo.Email;
import org.example.catch_line.member.model.vo.Password;
import org.example.catch_line.member.model.vo.PhoneNumber;
import org.example.catch_line.member.validation.MemberValidator;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.example.catch_line.member.model.entity.MemberEntity;
import org.example.catch_line.member.repository.MemberRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.junit.jupiter.api.extension.ExtendWith;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private MemberValidator memberValidator;  // MemberValidator를 Mock으로 주입

    @InjectMocks
    private MemberService memberService;

    private MemberEntity defaultMember;

    @BeforeEach
    public void setup() {
        // 실제 Bcrypt 형식의 암호화된 패스워드 생성
        String rawPassword = "1234!@#$qwer";
        String encodedPassword = new BCryptPasswordEncoder().encode(rawPassword); // 실제 암호화된 패스워드 생성

        given(bCryptPasswordEncoder.encode(anyString())).willReturn(encodedPassword);

        defaultMember = MemberEntity.builder()
                .email(new Email("test@gmail.com"))
                .password(new Password(encodedPassword))
                .phoneNumber(new PhoneNumber("010-1212-3434"))
                .nickname("test nickname")
                .name("test name")
                .role(Role.USER)
                .build();

        given(memberValidator.checkIfMemberPresent(1L))
                .willReturn(defaultMember);
    }

    @DisplayName("회원정보 수정 성공 테스트")
    @Test
    public void member_update_success_test() {

        MemberUpdateRequest memberUpdateRequest =
                MemberUpdateRequest.builder()
                        .email("test_update@gmail.com")
                        .name("update test name")
                        .nickname("update test nickname")
                        .password("1234!@#$qwert")
                        .phoneNumber("010-2121-4343")
                        .build();

        memberService.updateMember(memberUpdateRequest, 1L);

        // 검증
        assertEquals("test_update@gmail.com", defaultMember.getEmail().getEmailValue());
        assertEquals("update test name", defaultMember.getName());
        assertEquals("update test nickname", defaultMember.getNickname());
        assertEquals(defaultMember.getPassword().getEncodedPassword(), defaultMember.getPassword().getEncodedPassword());
        assertEquals("010-2121-4343", defaultMember.getPhoneNumber().getPhoneNumberValue());
        assertEquals(Role.USER, defaultMember.getRole());
    }
}
