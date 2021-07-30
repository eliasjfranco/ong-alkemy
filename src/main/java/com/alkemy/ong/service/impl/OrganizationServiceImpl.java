package com.alkemy.ong.service.impl;

import com.alkemy.ong.dto.request.OrganizationRequestDto;
import com.alkemy.ong.dto.SocialNetworkDto;
import com.alkemy.ong.dto.response.OrganizationResponseDto;
import com.alkemy.ong.model.Organization;
import com.alkemy.ong.model.SocialNetwork;
import com.alkemy.ong.repository.OrganizationRepository;
import com.alkemy.ong.repository.SocialNetworkRepository;
import com.alkemy.ong.service.Interface.IFileStore;
import com.alkemy.ong.service.Interface.IOrganization;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.EntityNotFoundException;

@Service
public class OrganizationServiceImpl implements IOrganization {

    private final OrganizationRepository repository;
    private final SocialNetworkRepository repoContact;
	private final MessageSource messageSource;
	private final ProjectionFactory projectionFactory;
	private final IFileStore fileStore;
	private final ModelMapper mapper;

	@Autowired
	public OrganizationServiceImpl(OrganizationRepository repository, SocialNetworkRepository repoContact, MessageSource messageSource, ProjectionFactory projectionFactory, IFileStore fileStore, ModelMapper mapper) {
		this.repository = repository;
		this.repoContact = repoContact;
		this.messageSource = messageSource;
		this.projectionFactory = projectionFactory;
		this.fileStore = fileStore;
		this.mapper = mapper;
	}


	@Override
    public List<OrganizationResponseDto> getAll() {
        return repository.findAllProjectedBy();
    }

	@Override
	public Organization getById(Long id) {
		return repository.findById(id).orElseThrow(
				() -> new EntityNotFoundException(messageSource.getMessage("organization.error.registered", null, Locale.getDefault()))
		);
	}

	@Override
	public String deleteOrganization(Long id) {
		Organization organization = getById(id);
		fileStore.deleteFilesFromS3Bucket(organization);
		return messageSource.getMessage(
			"organization.delete.successful", null, Locale.getDefault()
		);
	}

	@Override
	public OrganizationResponseDto updateOrg(Long id, OrganizationRequestDto org) {

		Organization organization = getById(id);
	
		organization.setName(org.getName());
		organization.setAddress(org.getAddress());
		organization.setPhone(org.getPhone());
		organization.setWelcomeText(org.getWelcomeText());
		organization.setAboutUsText(org.getAboutUsText());
		organization.setEdited(new Date());
		if(org.getImage() != null)
			organization.setImage(fileStore.save(organization, org.getImage()));

		return projectionFactory.createProjection(OrganizationResponseDto.class, repository.save(organization));
	}

	@Override
	public SocialNetworkDto newContact(Long id, SocialNetworkDto dto) {
		
		Organization org = this.getById(id);
			
		SocialNetwork contact = new SocialNetwork();

		if(!dto.getName().isBlank())
			contact.setName(dto.getName());
		if(!dto.getLink().isBlank())
			contact.setLink(dto.getLink());
		
		contact.setOrg(org);
		org.getContact().add(contact);

		repository.save(org);
		return mapper.map(repoContact.save(contact), SocialNetworkDto.class);
	}

	@Override
	public OrganizationResponseDto newOrg(OrganizationRequestDto organizationRequestDto) {
		
		Organization organization = Organization.builder()
				.aboutUsText(organizationRequestDto.getAboutUsText())
				.address(organizationRequestDto.getAddress())
				.email(organizationRequestDto.getEmail())
				.name(organizationRequestDto.getName())
				.welcomeText(organizationRequestDto.getWelcomeText())
				.phone(organizationRequestDto.getPhone())
				.build();

		Organization organizationCreated = repository.save(organization);
		
		if(organizationRequestDto.getImage() != null)
			organizationCreated.setImage(fileStore.save(organizationCreated, organizationRequestDto.getImage()));

		return projectionFactory.createProjection(OrganizationResponseDto.class, repository.save(organizationCreated));
	}

}
