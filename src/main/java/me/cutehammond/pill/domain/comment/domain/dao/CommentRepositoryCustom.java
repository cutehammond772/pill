package me.cutehammond.pill.domain.comment.domain.dao;

import me.cutehammond.pill.domain.comment.domain.Comment;
import me.cutehammond.pill.domain.pill.domain.Pill;
import me.cutehammond.pill.domain.user.domain.User;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentRepositoryCustom {

    List<Comment> getPillComments(Pill pill, Pageable pageable);

    List<Comment> getUserComments(User user, Pageable pageable);

}
