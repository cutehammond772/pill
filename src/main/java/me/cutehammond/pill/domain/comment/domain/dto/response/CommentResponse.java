package me.cutehammond.pill.domain.comment.domain.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.domain.comment.domain.Comment;
import me.cutehammond.pill.domain.like.domain.CommentLike;
import me.cutehammond.pill.domain.user.domain.dto.response.UserProfileResponse;
import me.cutehammond.pill.domain.user.domain.dto.response.UserResponse;

import java.util.List;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentResponse {

    @NonNull
    private final Long commentId;

    @NonNull
    private final Long pillId;

    @NonNull
    private final String userId;

    @NonNull
    private final String comment;

    @NonNull
    private final List<UserProfileResponse> likedUsers;

    public static CommentResponse from(@NonNull Comment comment) {
        return new CommentResponse(comment.getId(), comment.getPill().getId(), comment.getUser().getUserId(), comment.getComment(),
                comment.getLikedUsers().stream()
                        .map(CommentLike::getUser)
                        .map(UserResponse::from)
                        .map(UserProfileResponse::from)
                        .toList());
    }

}
