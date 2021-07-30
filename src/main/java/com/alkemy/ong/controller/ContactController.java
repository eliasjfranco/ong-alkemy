package com.alkemy.ong.controller;

import com.alkemy.ong.dto.request.ContactRequestDto;
import com.alkemy.ong.dto.response.ContactResponseDto;
import com.alkemy.ong.service.Interface.IContact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/contacts")
public class ContactController {

    private final IContact contactService;
    private final MessageSource message;

    @Autowired
    public ContactController(IContact contactService, MessageSource message) {
        this.contactService = contactService;
        this.message = message;
    }

    @PostMapping
    public ResponseEntity<?> post(@Valid @RequestBody ContactRequestDto dto){
        try {
            return ResponseEntity.ok(contactService.createContacts(dto));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<ContactResponseDto>> getAll(){
        return ResponseEntity.status(HttpStatus.OK).body(contactService.getAllContacts());
    }
}
