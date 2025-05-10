package com.yurdan.authService.model;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
public class LoginRequest {

    private  String email;
    private  String password;

}
