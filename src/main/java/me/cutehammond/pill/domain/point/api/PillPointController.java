package me.cutehammond.pill.domain.point.api;

import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.domain.point.application.PillPointService;
import me.cutehammond.pill.domain.point.domain.PillPointOrder;
import me.cutehammond.pill.domain.point.domain.dto.request.PillPointSpecRequest;
import me.cutehammond.pill.domain.point.domain.dto.response.PillPointResponse;
import me.cutehammond.pill.domain.point.exception.PillPointOutOfBoundsException;
import me.cutehammond.pill.domain.point.exception.particular.PillPointUsingFailedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/point")
@RequiredArgsConstructor
public class PillPointController {

    private final PillPointService pillPointService;

    /* for admin */
    @PostMapping(value = {"/", "/{userId}"})
    public ResponseEntity<?> addPoint(@PathVariable(name = "userId", required = false) Optional<String> userId, @RequestBody long specId) {
        pillPointService.addPoint(userId, specId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/spec")
    public ResponseEntity<?> addPointSpec(@RequestBody PillPointSpecRequest pillPointSpecRequest) {
        pillPointService.addPointSpec(pillPointSpecRequest);
        return ResponseEntity.ok().build();
    }

    /* for default user */

    @PatchMapping(value = {"/", "/{userId}"})
    public ResponseEntity<?> usePoints(@PathVariable(name = "userId", required = false) Optional<String> userId, @RequestBody int point) {
        /* point validation */
        if (point <= 0)
            throw new PillPointOutOfBoundsException("사용할 포인트가 0 이하입니다.");

        pillPointService.usePoints(userId, point);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = {"/", "/{userId}"})
    public ResponseEntity<Integer> getPointsLeft(@PathVariable(name = "userId", required = false) Optional<String> userId) {
        int points = pillPointService.getPointsLeft(userId);

        return ResponseEntity.ok(points);
    }

    @GetMapping(value = {"/list", "/list/{userId}"})
    public ResponseEntity<?> getPoints(@PathVariable(name = "userId", required = false) Optional<String> userId,
                                       @RequestParam(name = "includeRunOut", required = false) boolean includeRunOut,
                                       @RequestParam(name = "includeExpired", required = false) boolean includeExpired) {

        /* PillPointOrder 설정은 나중에 한다. */
        var points = pillPointService
                .getPoints(userId, includeRunOut, includeExpired, PillPointOrder.CLOSE_TO_RECEIVED);

        var map = points.stream()
                .collect(Collectors.toMap(PillPointResponse::getName, Function.identity()));

        return ResponseEntity.ok(map);
    }

}
