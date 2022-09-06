package me.cutehammond.pill.domain.pill.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.cutehammond.pill.domain.pill.domain.dto.PillUpdateRequest;
import me.cutehammond.pill.domain.user.domain.User;
import me.cutehammond.pill.global.common.BaseTimeEntity;
import org.bson.types.ObjectId;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "pill_table")
public class Pill extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sequence")
    private Long pillSequence;

    @NotNull
    @Column(nullable = false, name = "title")
    @Size(min = 2, max = 100)
    private String title;

    @NotNull
    @Column(nullable = false, name = "root_element", unique = true)
    private ObjectId rootElement;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_sequence")
    private User user;

    @Builder
    public Pill(String title, ObjectId rootElement, User user) {
        this.title = title;
        this.rootElement = rootElement;
        this.user = user;
    }

    // domain logic
    public final void update(PillUpdateRequest pillUpdateRequest) {
        /* changing a title */
        this.title = pillUpdateRequest.getTitle();

    }

}
