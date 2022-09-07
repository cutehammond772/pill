package me.cutehammond.pill.domain.pill.domain;

import lombok.*;
import me.cutehammond.pill.domain.pill.domain.dto.PillElementRequest;
import me.cutehammond.pill.global.common.BaseTimeEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "test")
public class PillElement extends BaseTimeEntity {

    @Id
    private ObjectId id;

    @NotNull
    @Field(name = "element_type")
    private PillElementType pillElementType;

    @NotNull
    @Field(name = "content")
    private String content;

    @Getter(value = AccessLevel.NONE)
    @Field(name = "siblings")
    private List<ObjectId> siblings = new ArrayList<>();

    @Builder
    public PillElement(PillElementType pillElementType, String content) {
        if (pillElementType == PillElementType.ROOT)
            throw new IllegalArgumentException("You cannot modify its element type to ROOT except 'PillElement.createRootElement()' method.");

        this.pillElementType = pillElementType;
        this.content = content;
    }

    public static final PillElement createRootElement() {
        PillElement element = builder()
                .pillElementType(PillElementType.SUBJECT) // This value will be modified to ROOT
                .content("ROOT").build();

        // This method can only set elementType to ROOT
        element.pillElementType = PillElementType.ROOT;
        return element;
    }

    public final Collection<ObjectId> getSiblings() {
        return Collections.unmodifiableList(siblings);
    }

    public final void addSiblingBack(PillElement element) {
        siblings.add(element.getId());
    }

    public final void addSiblingInto(int index, PillElement element) {
        siblings.add(index, element.getId());
    }

    public final void edit(PillElementRequest request) {
        if (request.getPillElementType() == PillElementType.ROOT)
            throw new IllegalArgumentException("You cannot modify its element type to ROOT except 'PillElement.createRootElement()' method.");

        this.pillElementType = request.getPillElementType();
        this.content = request.getContent();
    }

}
