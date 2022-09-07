package me.cutehammond.pill.domain.pill.application;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.cutehammond.pill.domain.pill.domain.Pill;
import me.cutehammond.pill.domain.pill.domain.PillElement;
import me.cutehammond.pill.domain.pill.domain.PillElementType;
import me.cutehammond.pill.domain.pill.domain.PillElementUpdateType;
import me.cutehammond.pill.domain.pill.domain.dao.nosql.PillElementRepository;
import me.cutehammond.pill.domain.pill.domain.dao.sql.PillRepository;
import me.cutehammond.pill.domain.pill.domain.dto.*;
import me.cutehammond.pill.domain.user.application.UserService;
import me.cutehammond.pill.domain.user.domain.User;
import me.cutehammond.pill.domain.user.domain.dao.sql.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PillService {

    private final PillRepository pillRepository;
    private final PillElementRepository elementRepository;

    private final UserService userService;

    @Transactional(readOnly = true)
    public PillElementResponse getPillElement(@NonNull ObjectId elementId) {
        Optional<PillElement> optionalPillElement = elementRepository.findById(elementId);

        if (optionalPillElement.isEmpty())
            throw new NoSuchElementException("An element with this 'objectId' does not exist: " + elementId.toHexString());

        PillElement element = optionalPillElement.get();

        // Single (no siblings)
        if (element.getSiblings().isEmpty())
            return PillElementResponse.single()
                    .elementType(element.getPillElementType())
                    .content(element.getContent())
                    .id(element.getId()).build();

        // Recursive
        List<PillElementResponse> siblings = element.getSiblings().stream()
                .map(this::getPillElement)
                .collect(Collectors.toList());

        return PillElementResponse.builder()
                .pillElementType(element.getPillElementType())
                .content(element.getContent())
                .id(element.getId())
                .siblings(siblings).build();
    }

    @Transactional(readOnly = true)
    public PillResponse getPill(@NonNull Long id) {
        Optional<Pill> optionalPill = pillRepository.findById(id);

        if (optionalPill.isEmpty())
            throw new NoSuchElementException("A pill does not exist: " + id);

        Pill pill = optionalPill.get();
        PillResponse response = new PillResponse(pill.getPillSequence(), pill.getTitle(), pill.getRootElement(), pill.getUser().getUserId());

        return response;
    }

    @Transactional
    public long update(@NonNull PillUpdateRequest request) {
        Optional<Pill> optionalPill = pillRepository.findById(request.getId());

        if (optionalPill.isEmpty())
            throw new NoSuchElementException("A pill does not exist: " + request.getId());

        Pill pill = optionalPill.get();
        pill.update(request);

        return pill.getPillSequence();
    }

    @Transactional
    public ObjectId updatePillElement(@NonNull PillElementRequest request, @NonNull PillElementUpdateType updateType) {
        Optional<PillElement> optionalPillElement = elementRepository.findById(request.getId());

        if (optionalPillElement.isEmpty())
            throw new NoSuchElementException("An element with this 'objectId' does not exist: " + request.getId().toHexString());

        PillElement element = optionalPillElement.get();

        if (updateType == PillElementUpdateType.RENEW)
            updateElementsRecursively(element, request);
        else if (updateType == PillElementUpdateType.UPDATE)
            renewElementsRecursively(element, request);

        return element.getId();
    }

    @Transactional
    public long createPill(PillCreateRequest pillCreateRequest, PillElementRequest pillElementRequest) {
        if (pillElementRequest.getPillElementType() != PillElementType.ROOT)
            throw new IllegalArgumentException("An element type of root element must be 'ROOT'.");

        PillElement rootElement = PillElement.createRootElement();
        rootElement = elementRepository.save(rootElement);

        pillElementRequest.getSiblings().stream()
                .map(this::saveElementsRecursively)
                .forEach(rootElement::addSiblingBack);

        User user = userService.getUser(pillCreateRequest.getUserId());

        Pill pill = Pill.builder()
                .title(pillCreateRequest.getTitle())
                .user(user)
                .rootElement(rootElement.getId()).build();

        pill = pillRepository.save(pill);
        return pill.getPillSequence();
    }

    @Transactional
    public long deletePill(PillDeleteRequest request) {
        Optional<Pill> optionalPill = pillRepository.findById(request.getId());

        if (optionalPill.isEmpty())
            throw new NoSuchElementException("A pill does not exist: " + request.getId());

        Pill pill = optionalPill.get();

        PillElementRequest pillElementRequest = PillElementRequest.toRequest(getPillElement(pill.getRootElement()));
        deleteElementsRecursively(pillElementRequest);

        pillRepository.delete(pill);
        return pill.getPillSequence();
    }

    private void updateElementsRecursively(PillElement element, PillElementRequest request) {
        if (request.getSiblings().isEmpty())
            return;

        Iterator<PillElementRequest> it = request.getSiblings().iterator();
        PillElementRequest r = it.next();

        for (ObjectId objectId : element.getSiblings()) {
            // null-safe
            PillElement e = elementRepository.findById(objectId).get();
            if (e.getId().equals(r.getId())) {
                e.edit(r);
                updateElementsRecursively(e, r);

                if (it.hasNext())
                    r = it.next();
                else
                    break;
            }
        }
    }

    private void renewElementsRecursively(PillElement element, PillElementRequest request) {
        deleteElementsRecursively(PillElementRequest.toRequest(getPillElement(element.getId())));

        request.getSiblings().stream()
                .map(this::saveElementsRecursively)
                .forEach(element::addSiblingBack);
    }

    private void deleteElementsRecursively(PillElementRequest request) {
        if (!request.getSiblings().isEmpty())
            request.getSiblings().forEach(this::deleteElementsRecursively);

        elementRepository.deleteById(request.getId());
    }

    private PillElement saveElementsRecursively(PillElementRequest element) {
        List<PillElement> siblings = new ArrayList<>();
        if (!element.getSiblings().isEmpty())
            siblings.addAll(
                    element.getSiblings().stream()
                            .map(this::saveElementsRecursively)
                            .toList()
            );

        PillElement pillElement = PillElement.builder()
                .pillElementType(element.getPillElementType())
                .content(element.getContent()).build();

        siblings.forEach(pillElement::addSiblingBack);
        return elementRepository.save(pillElement);
    }

}
