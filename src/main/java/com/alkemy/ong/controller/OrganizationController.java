package com.alkemy.ong.controller;

import com.alkemy.ong.dto.OrganizationDto;
import com.alkemy.ong.service.impl.OrganizationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OrganizationController {

    @Autowired
    OrganizationServiceImpl organizationService;

    @RequestMapping(value = "/organization/public", method = RequestMethod.GET)
    public ResponseEntity<?> getOrganizationData(){
        List<OrganizationDto> dto = organizationService.getAll();

        return ResponseEntity.ok(dto);

    }

    }
