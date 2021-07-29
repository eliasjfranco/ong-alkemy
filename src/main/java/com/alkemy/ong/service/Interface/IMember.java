package com.alkemy.ong.service.Interface;

import com.alkemy.ong.dto.request.MemberRequestDto;
import com.alkemy.ong.dto.response.MemberResponseDto;
import com.alkemy.ong.model.Member;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IMember {

    Page<Member> showAllMembers(Pageable pageable);

    MemberResponseDto createMember(MemberRequestDto member);

    MemberResponseDto updateMemberById(Long id, MemberRequestDto dto);

    String deleteMember(Long id);

    Member getMemberById(Long id);
}
