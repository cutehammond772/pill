package me.cutehammond.pill.domain.pill.api;

import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.domain.pill.application.PillService;
import me.cutehammond.pill.domain.pill.domain.PillElementUpdateType;
import me.cutehammond.pill.domain.pill.domain.dto.*;
import me.cutehammond.pill.global.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/pill")
@RequiredArgsConstructor
public class PillController {

    private final PillService pillService;

    @PostMapping
    public ResponseEntity createPill(@RequestBody PillCreateRequest createRequest, @RequestBody PillElementRequest elementRequest) {
        // pillSequence (= id)
        long id = pillService.createPill(createRequest, elementRequest);

        return ApiResponse.success(Map.of("id", id));
    }

    @PutMapping("/{type}")
    public ResponseEntity updatePill(@RequestBody PillUpdateRequest updateRequest, @RequestBody PillElementRequest elementRequest, @PathVariable String type) {
        // pillSequence (= id)
        long id = pillService.update(updateRequest);

        PillElementUpdateType updateType = PillElementUpdateType.valueOf(type);
        pillService.updatePillElement(elementRequest, updateType);

        return ApiResponse.success(Map.of("id", id));
    }

    @GetMapping("/{id}")
    public ResponseEntity find(@PathVariable Long id) {
        PillResponse response = pillService.getPill(id);

        return ApiResponse.success(Map.of("response", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        PillResponse response = pillService.getPill(id);
        pillService.deletePill(new PillDeleteRequest(id));

        return ApiResponse.success(Map.of("response", response));
    }

}
