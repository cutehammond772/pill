package me.cutehammond.pill.domain.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import me.cutehammond.pill.global.common.BaseTimeEntity;
import me.cutehammond.pill.global.oauth.entity.Provider;
import me.cutehammond.pill.global.oauth.entity.Role;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Getter
@Entity
@Table(name = "user_table")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @JsonIgnore
    @Id
    @Column(name = "sequence")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userSequence;

    @NotNull
    @Column(name = "id", length = 64, unique = true)
    private String userId;

    @NotNull
    @Setter
    @Column(name = "name", length = 20)
    private String userName;

    @NotNull
    @Column(name = "email", length = 128, unique = true)
    private String email;

    @Setter
    @Column(name = "profile_url", length = 512)
    private String profileUrl;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "provider", length = 20)
    private Provider provider;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20)
    private Role role;

    @Builder
    public User(String userId, String userName, String email, String profileUrl, Provider provider, Role role) {
        this.userId = userId;
        this.userName = userName;
        this.email = Optional.ofNullable(email).orElse("NO_EMAIL");
        this.profileUrl = profileUrl;
        this.provider = provider;
        this.role = role;
    }



}
