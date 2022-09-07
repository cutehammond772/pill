package me.cutehammond.pill.domain.pill.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import me.cutehammond.pill.domain.pill.domain.PillElementType;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

@Getter
public final class PillElementRequest {

    @NonNull
    private final PillElementType pillElementType;

    @NonNull
    private final ObjectId id;

    @NonNull
    private final String content;

    @NonNull
    private final List<PillElementRequest> siblings = new ArrayList<>();

    @Builder
    public PillElementRequest(PillElementType pillElementType, ObjectId id, String content) {
        this.pillElementType = pillElementType;
        this.id = id;
        this.content = content;
    }

    public static PillElementRequest toRequest(PillElementResponse response) {
        PillElementRequest request = PillElementRequest.builder()
                .pillElementType(response.getPillElementType())
                .id(response.getId())
                .content(response.getContent()).build();

        request.getSiblings().addAll(
                response.getSiblings().stream().map(PillElementRequest::toRequest).toList()
        );

        return request;
    }

}
