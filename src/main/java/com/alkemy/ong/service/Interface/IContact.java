package com.alkemy.ong.service.Interface;

import com.alkemy.ong.dto.request.ContactRequestDto;
import com.alkemy.ong.dto.response.ContactResponseDto;

import java.util.List;

public interface IContact {

    ContactResponseDto createContacts(ContactRequestDto dto);

    List<ContactResponseDto> getAllContacts();

}
