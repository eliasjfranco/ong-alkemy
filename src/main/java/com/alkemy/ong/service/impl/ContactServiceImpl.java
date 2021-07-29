package com.alkemy.ong.service.impl;

import com.alkemy.ong.dto.request.ContactRequestDto;
import com.alkemy.ong.dto.response.ContactResponseDto;
import com.alkemy.ong.model.Contact;
import com.alkemy.ong.repository.ContactRepository;
import com.alkemy.ong.service.Interface.IContact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactServiceImpl implements IContact {

    private final ContactRepository repository;
    private final  ProjectionFactory projectionFactory;

    @Autowired
    public ContactServiceImpl(ContactRepository repository, ProjectionFactory projectionFactory) {
        this.repository = repository;
        this.projectionFactory = projectionFactory;
    }

    @Override
    public ContactResponseDto createContacts(ContactRequestDto dto) {
        Contact contact = new Contact(
                dto.getName(),
                dto.getPhone(),
                dto.getEmail(),
                dto.getMessage()
        );

        return projectionFactory.createProjection(ContactResponseDto.class, repository.save(contact));
    }

    @Override
    public List<ContactResponseDto> getAllContacts() {
        return repository.findAllProjectedBy();
    }

}