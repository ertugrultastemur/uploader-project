package com.example.usermanagementservice.dto;

import com.example.usermanagementservice.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {

    private int id;
    private String email;
    private String password;
    private String role;

    public static UserDto convert(User user){
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getRole()
        );
    }
}
