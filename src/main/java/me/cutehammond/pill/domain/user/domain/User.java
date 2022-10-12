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

    /**
     * 사용자의 고유 id를 나타냅니다.
     */
    @NotNull
    @Column(name = "id", length = 64, unique = true)
    private String userId;

    /**
     * 사용자의 별칭(닉네임)을 나타냅니다. userId와 다르게 임의로 변경이 가능합니다.
     */
    @NotNull
    @Setter
    @Column(name = "name", length = 20)
    private String userName;

    /**
     * 사용자의 이메일 주소입니다. 이후 여러 검증에 사용될 수 있습니다.
     */
    @NotNull
    @Column(name = "email", length = 128, unique = true)
    private String email;

    /**
     * provider로부터 가져온 프로파일 리소스입니다.
     */
    @Setter
    @Column(name = "profile_url", length = 512)
    private String profileUrl;

    /**
     * 회원가입 시 선택한 provider를 나타냅니다.
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "provider", length = 20)
    private Provider provider;

    /**
     * 사용자의 권한을 나타냅니다.
     */
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
