package org.example.catch_line.member.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.catch_line.member.model.dto.MemberResponse;
import org.example.catch_line.member.model.dto.MemberUpdateRequest;
import org.example.catch_line.member.model.entity.MemberEntity;
import org.example.catch_line.member.model.mapper.MemberResponseMapper;
import org.example.catch_line.member.model.vo.Email;
import org.example.catch_line.member.model.vo.PhoneNumber;
import org.example.catch_line.member.repository.MemberRepository;
import org.example.catch_line.member.validate.MemberValidator;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberValidator memberValidator;


    // 회원 정보 조회
    public MemberResponse findMember(Long memberId) {
        MemberEntity member = memberValidator.checkIfMemberPresent(memberId);
        return MemberResponseMapper.entityToResponse(member);
    }

    // 회원 정보 수정
    public MemberResponse updateMember(MemberUpdateRequest updateMemberRequest, Long memberId) {
        // 존재하는 회원인지 확인하는 메서드를 따로 추가했습니다. (이유: 회원 탈퇴 시에도 필요)
        MemberEntity member = memberValidator.checkIfMemberPresent(memberId);

        // 이메일을 수정하였다면, 이미 존재하는 이메일인지 검증이 필요합니다.
        // 수정되지 않은 경우, 검증이 불필요합니다.
        Email email = new Email(updateMemberRequest.getEmail());
        PhoneNumber phoneNumber = new PhoneNumber(updateMemberRequest.getPhoneNumber());

        if (!member.getEmail().getEmailValue().equals(updateMemberRequest.getEmail()))
            memberValidator.checkDuplicateEmail(email);

        // TODO: 해당 부분 리팩토링이 가능한지 -> 타입 안전성
        member.updateMember(email, updateMemberRequest.getName(), updateMemberRequest.getNickname(), updateMemberRequest.getPassword(), phoneNumber);

        memberRepository.save(member);
        return MemberResponseMapper.entityToResponse(member);
    }

    // 회원 탈퇴 (`status`만 변경)
    // TODO: 탈퇴 시 로그아웃까지 되어야 함
    public MemberResponse deleteMember(Long memberId) {
        MemberEntity member = memberValidator.checkIfMemberPresent(memberId);

        member.doWithdrawal();
        return MemberResponseMapper.entityToResponse(member);


    }


}
