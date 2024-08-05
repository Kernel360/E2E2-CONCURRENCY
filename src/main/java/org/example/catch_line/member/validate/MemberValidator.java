package org.example.catch_line.member.validate;

import lombok.RequiredArgsConstructor;
import org.example.catch_line.member.model.entity.MemberEntity;
import org.example.catch_line.member.repository.MemberRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberValidator {

    private final MemberRepository memberRepository;

    // TODO: 탈퇴한 회원의 이메일이 db에 남아있음. 이들은 제외하고 이메일 중복 체크 해야 함.
    public void checkDuplicateEmail(String email) {
        if (memberRepository.findByEmailAndIsMemberDeletedFalse(email).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
    }

    // TODO: 탈퇴한 회원은 제외하고 회원 존재 여부 검사
    public MemberEntity checkIfMemberPresent(Long memberId) {
        return memberRepository.findByMemberIdAndIsMemberDeletedFalse(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 사용자가 없습니다."));
    }
}
