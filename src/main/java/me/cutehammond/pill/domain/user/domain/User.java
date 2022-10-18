package me.cutehammond.pill.domain.user.domain;

import lombok.*;
import me.cutehammond.pill.domain.comment.domain.Comment;
import me.cutehammond.pill.domain.like.domain.CommentLike;
import me.cutehammond.pill.domain.like.domain.PillLike;
import me.cutehammond.pill.domain.pill.domain.Pill;
import me.cutehammond.pill.domain.point.domain.PillPointContainer;
import me.cutehammond.pill.domain.user.domain.dto.request.UserRegisterRequest;
import me.cutehammond.pill.domain.user.domain.dto.request.UserUpdateRequest;
import me.cutehammond.pill.global.common.BaseTimeEntity;
import me.cutehammond.pill.global.oauth.entity.Provider;
import me.cutehammond.pill.global.oauth.entity.Role;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "USER_TABLE")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 사용자의 고유 id를 나타냅니다.
     */
    @Getter
    @NotBlank
    @Column(nullable = false, length = 64, unique = true)
    private String userId;

    /**
     * 사용자의 별칭(닉네임)을 나타냅니다. userId와 다르게 임의로 변경이 가능합니다.
     */
    @Getter
    @NotBlank
    @Column(nullable = false, length = 20)
    private String userName;

    /**
     * 사용자의 이메일 주소입니다. 이후 여러 검증에 사용될 수 있습니다.
     */
    @Getter
    @Email
    @Column(length = 128, unique = true)
    private String email;

    /**
     * provider로부터 가져온 프로파일 리소스입니다.
     */
    @Getter
    @Column(nullable = false, length = 512)
    private String profileUrl;

    /**
     * 회원가입 시 선택한 provider를 나타냅니다.
     */
    @Getter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Provider provider;

    /**
     * 사용자의 권한을 나타냅니다.
     */
    @Getter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    /**
     * 사용자가 좋아요를 눌렀던 댓글의 모음을 나타냅니다.
     */
    @Getter
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<CommentLike> likedComments = new ArrayList<>();

    /**
     * 사용자가 작성한 댓글의 모음을 나타냅니다.
     */
    @Getter
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Comment> comments = new ArrayList<>();

    /**
     * 사용자가 좋아요를 눌렀던 Pill의 모음을 나타냅니다.
     */
    @Getter
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<PillLike> likedPills = new ArrayList<>();

    /**
     * 사용자가 만든 Pill의 모음을 나타냅니다.
     */
    @Getter
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Pill> pills = new ArrayList<>();

    /**
     * 사용자의 포인트를 관리하는 엔티티입니다.
     */
    @Getter
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pillPointContainer", nullable = false)
    private PillPointContainer pillPointContainer;

    public User(@NonNull UserRegisterRequest userRegisterRequest) {
        this.userId = userRegisterRequest.getUserId();
        this.userName = userRegisterRequest.getUserName();
        this.email = userRegisterRequest.getEmail();
        this.profileUrl = userRegisterRequest.getProfileUrl();
        this.provider = userRegisterRequest.getProvider();
        this.role = userRegisterRequest.getRole();
        this.pillPointContainer = new PillPointContainer(this);
    }

    public final void update(UserUpdateRequest userUpdateRequest) {
        if (userUpdateRequest.getUserName() != null)
            this.userName = userUpdateRequest.getUserName();

        if (userUpdateRequest.getRole() != null)
            this.role = userUpdateRequest.getRole();

        if (userUpdateRequest.getProfileUrl() != null)
            this.profileUrl = userUpdateRequest.getProfileUrl();
    }

}
