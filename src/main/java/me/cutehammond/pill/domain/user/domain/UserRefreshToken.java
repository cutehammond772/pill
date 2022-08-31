package me.cutehammond.pill.domain.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "user_refresh_token_table")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRefreshToken {

    @JsonIgnore
    @Id
    @Column(name = "sequence")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refreshTokenSequence;

    @NotNull
    @Column(name = "id", length = 64, unique = true)
    private String userId;

    @Column(name = "refresh_token", length = 256)
    @NotNull
    @Setter
    private String refreshToken;

    @Builder
    public UserRefreshToken(String userId, String refreshToken) {
        this.userId = userId;
        this.refreshToken = refreshToken;
    }

}
