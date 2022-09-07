package me.cutehammond.pill.domain.pill.domain.dto;

import lombok.*;
import me.cutehammond.pill.domain.pill.domain.PillElementType;
import org.bson.types.ObjectId;

import java.util.Collections;
import java.util.List;

@Getter
public final class PillElementResponse {

    @NonNull
    private final PillElementType pillElementType;

    @NonNull
    private final String content;

    @NonNull
    private final ObjectId id;

    @NonNull
    private final List<PillElementResponse> siblings;

    @Builder(builderClassName = "PillElementResponseBuilder")
    public PillElementResponse(PillElementType pillElementType, String content, ObjectId id, List<PillElementResponse> siblings) {
        this.pillElementType = pillElementType;
        this.content = content;
        this.id = id;
        this.siblings = Collections.unmodifiableList(siblings);
    }

    @Builder(builderClassName = "SinglePillElementResponseBuilder", builderMethodName = "single")
    public PillElementResponse(PillElementType elementType, String content, ObjectId id) {
        this(elementType, content, id, List.of());
    }

}
