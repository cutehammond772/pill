package me.cutehammond.pill.domain.comment.domain.dao;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.domain.comment.domain.Comment;
import me.cutehammond.pill.domain.pill.domain.Pill;
import me.cutehammond.pill.domain.user.domain.User;
import org.springframework.data.domain.Pageable;

import java.util.List;
import static me.cutehammond.pill.domain.comment.domain.QComment.*;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Comment> getPillComments(Pill pill, Pageable pageable) {
        return jpaQueryFactory.selectFrom(comment1)
                .where(comment1.pill.eq(pill))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public List<Comment> getUserComments(User user, Pageable pageable) {
        return jpaQueryFactory.selectFrom(comment1)
                .where(comment1.user.eq(user))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }
}
