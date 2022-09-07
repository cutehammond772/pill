package me.cutehammond.pill.domain.pill.domain.dao.nosql;

import me.cutehammond.pill.domain.pill.domain.PillElement;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PillElementRepository extends MongoRepository<PillElement, ObjectId> {
}
