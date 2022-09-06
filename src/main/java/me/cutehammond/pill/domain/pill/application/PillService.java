package me.cutehammond.pill.domain.pill.application;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.domain.pill.domain.Pill;
import me.cutehammond.pill.domain.pill.domain.PillElement;
import me.cutehammond.pill.domain.pill.domain.PillElementType;
import me.cutehammond.pill.domain.pill.domain.dao.nosql.PillElementRepository;
import me.cutehammond.pill.domain.pill.domain.dao.sql.PillRepository;
import me.cutehammond.pill.domain.pill.domain.dto.*;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PillService {

    private final PillRepository pillRepository;
    private final PillElementRepository elementRepository;

    @Transactional(readOnly = true)
    public PillElementResponse getPillElement(@NonNull ObjectId elementId) {
        Optional<PillElement> optionalPillElement = elementRepository.findById(elementId);

        if (optionalPillElement.isEmpty())
            throw new NullPointerException("An element with this 'objectId' does not exist: " + elementId.toHexString());

        PillElement element = optionalPillElement.get();

        // Single (no siblings)
        if (element.getSiblings().size() == 0)
            return PillElementResponse.single()
                    .elementType(element.getPillElementType())
                    .content(element.getContent()).build();

        // Recursive
        List<PillElementResponse> siblings = element.getSiblings().stream()
                .map(this::getPillElement)
                .collect(Collectors.toList());

        return PillElementResponse.builder()
                .pillElementType(element.getPillElementType())
                .content(element.getContent())
                .siblings(siblings).build();
    }

    @Transactional
    public long update(PillUpdateRequest request) {
        Optional<Pill> optionalPill = pillRepository.findById(request.getId());

        if (optionalPill.isEmpty())
            throw new NullPointerException("A pill does not exist: " + request.getId());

        Pill pill = optionalPill.get();
        pill.update(request);

        return pill.getPillSequence();
    }

    @Transactional
    public long createPill(PillCreateRequest pillCreateRequest, PillElementRequest pillElementRequest) {
        if (pillElementRequest.getPillElementType() != PillElementType.ROOT)
            throw new IllegalArgumentException("An element type of root element must be 'ROOT'.");

        PillElement rootElement = PillElement.createRootElement();
        rootElement = elementRepository.save(rootElement);

        pillElementRequest.getSiblings().stream()
                .map(this::saveElements)
                .forEach(rootElement::addSiblingBack);

        Pill pill = Pill.builder()
                .title(pillCreateRequest.getTitle())
                .user(pillCreateRequest.getUser())
                .rootElement(rootElement.getId()).build();

        pill = pillRepository.save(pill);
        return pill.getPillSequence();
    }

    private PillElement saveElements(PillElementRequest element) {
        List<PillElement> siblings = new ArrayList<>();
        if (element.getSiblings().size() != 0)
            siblings.addAll(
                    element.getSiblings().stream()
                            .map(this::saveElements)
                            .toList()
            );

        PillElement pillElement = PillElement.builder()
                .pillElementType(element.getPillElementType())
                .content(element.getContent()).build();

        siblings.forEach(pillElement::addSiblingBack);
        return elementRepository.save(pillElement);
    }

    @Transactional
    public long deletePill(PillDeleteRequest request) {
        Optional<Pill> optionalPill = pillRepository.findById(request.getId());

        if (optionalPill.isEmpty())
            throw new NullPointerException("A pill does not exist: " + request.getId());

        Pill pill = optionalPill.get();
        pillRepository.delete(pill);

        return pill.getPillSequence();
    }

}
