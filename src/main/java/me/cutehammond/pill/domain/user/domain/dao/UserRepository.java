package me.cutehammond.pill.domain.user.domain.dao;

import me.cutehammond.pill.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUserId(String userId);
    User findByEmail(String email);

}
