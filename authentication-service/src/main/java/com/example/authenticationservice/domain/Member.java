package com.example.authenticationservice.domain;



import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE mistakes SET is_deleted = true WHERE id=id")
@Where(clause = "is_deleted=false")
public class Member {

    public Member(String email, String password, Set<String> roles) {
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String email;

    @Column
    private String password;

    @ElementCollection
    private Set<String> roles;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;
    
}
