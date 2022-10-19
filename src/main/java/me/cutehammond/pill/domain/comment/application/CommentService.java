package me.cutehammond.pill.domain.comment.application;

import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.domain.comment.domain.Comment;
import me.cutehammond.pill.domain.comment.domain.dao.CommentRepository;
import me.cutehammond.pill.domain.comment.domain.dto.request.CommentRequest;
import me.cutehammond.pill.domain.comment.domain.dto.response.CommentResponse;
import me.cutehammond.pill.domain.comment.exception.CommentNotFoundException;
import me.cutehammond.pill.domain.pill.domain.Pill;
import me.cutehammond.pill.domain.pill.domain.dao.sql.PillRepository;
import me.cutehammond.pill.domain.pill.exception.PillNotFoundException;
import me.cutehammond.pill.domain.user.application.UserService;
import me.cutehammond.pill.domain.user.domain.User;
import me.cutehammond.pill.domain.user.domain.dao.sql.UserRepository;
import me.cutehammond.pill.domain.user.exception.UserNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PillRepository pillRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<CommentResponse> getPillComments(Long pillId, Pageable pageable) {
        Pill pill = pillRepository.findById(pillId)
                .orElseThrow(() -> new PillNotFoundException(pillId));

        return commentRepository.getPillComments(pill, pageable).stream()
                .map(CommentResponse::from).toList();
    }

    public List<CommentResponse> getUserComments(String userId, Pageable pageable) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return commentRepository.getUserComments(user, pageable).stream()
                .map(CommentResponse::from).toList();
    }

    @Transactional
    public void addComment(CommentRequest commentRequest) {
        Pill pill = pillRepository.findById(commentRequest.getPillId())
                .orElseThrow(() -> new PillNotFoundException(commentRequest.getPillId()));
        User user = userRepository.findByUserId(commentRequest.getUserId())
                .orElseThrow(() -> new UserNotFoundException(commentRequest.getUserId()));

        Comment comment = Comment.builder()
                .pill(pill).user(user).comment(commentRequest.getComment())
                .build();

        commentRepository.save(comment);
    }

    @Transactional
    public void removeComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        commentRepository.delete(comment);
    }
}
