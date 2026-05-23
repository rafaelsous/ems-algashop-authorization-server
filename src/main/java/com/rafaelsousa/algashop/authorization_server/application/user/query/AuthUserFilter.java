package com.rafaelsousa.algashop.authorization_server.application.user.query;

import com.rafaelsousa.algashop.authorization_server.domain.model.user.AuthUserType;

import lombok.*;
import org.springframework.data.domain.Sort;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthUserFilter {
    private String email;
    private AuthUserType type;
    
    private int page = 0;
    private int size = 10;
    private String sort = "name";
    private Sort.Direction direction = Sort.Direction.ASC;
}