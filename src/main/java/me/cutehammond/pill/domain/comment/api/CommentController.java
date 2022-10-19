package me.cutehammond.pill.domain.comment.api;

import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.domain.category.exception.CategoryInvalidPageRequestException;
import me.cutehammond.pill.domain.category.exception.InvalidCategoryException;
import me.cutehammond.pill.domain.comment.application.CommentService;
import me.cutehammond.pill.domain.comment.domain.dto.request.CommentRequest;
import me.cutehammond.pill.domain.comment.domain.dto.response.CommentResponse;
import me.cutehammond.pill.domain.comment.exception.CommentInvalidPageRequestException;
import me.cutehammond.pill.domain.comment.exception.InvalidCommentException;
import me.cutehammond.pill.domain.pill.exception.InvalidPillException;
import me.cutehammond.pill.domain.user.exception.InvalidUserException;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CommentResponse>> getUserComments(@PathVariable String userId,
                                                                 @RequestParam(name = "page") Integer page,
                                                                 @RequestParam(name = "size") Integer size) {
        if (!StringUtils.hasText(userId))
            throw new InvalidUserException(userId);
        if (!(page >= 0 && size > 0))
            throw new CommentInvalidPageRequestException(page, size);

        return ResponseEntity.ok(commentService.getUserComments(userId, PageRequest.of(page, size)));
    }

    @GetMapping("/pill/{pillId}")
    public ResponseEntity<List<CommentResponse>> getPillComments(@PathVariable Long pillId,
                                                                 @RequestParam(name = "page") Integer page,
                                                                 @RequestParam(name = "size") Integer size) {
        if (pillId < 0)
            throw new InvalidPillException(pillId);
        if (!(page >= 0 && size > 0))
            throw new CommentInvalidPageRequestException(page, size);

        return ResponseEntity.ok(commentService.getPillComments(pillId, PageRequest.of(page, size)));
    }

    @PostMapping
    public ResponseEntity<?> addComment(@RequestBody CommentRequest commentRequest) {
        if (!StringUtils.hasText(commentRequest.getComment()))
            throw new InvalidCategoryException(commentRequest.getComment());

        if (commentRequest.getPillId() < 0)
            throw new InvalidPillException(commentRequest.getPillId());

        if (!StringUtils.hasText(commentRequest.getUserId()))
            throw new InvalidUserException(commentRequest.getUserId());

        commentService.addComment(commentRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> removeComment(@PathVariable Long commentId) {
        if (commentId < 0)
            throw new InvalidCommentException(commentId);

        commentService.removeComment(commentId);
        return ResponseEntity.ok().build();
    }

}
