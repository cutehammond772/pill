package me.cutehammond.pill.domain.comment.domain.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class CommentRequest {

    @NonNull
    private final Long pillId;

    @NonNull
    private final String userId;

    @NonNull
    private final String comment;

}
