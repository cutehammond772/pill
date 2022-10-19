package me.cutehammond.pill.domain.comment.domain.dao;

import me.cutehammond.pill.domain.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {
}
