package me.cutehammond.pill.domain.pill.domain.dao.sql;

import me.cutehammond.pill.domain.pill.domain.Pill;
import org.bson.types.ObjectId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PillRepository extends JpaRepository<Pill, Long> {

    Optional<Pill> findByRootElement(ObjectId rootElement);

}
