package me.cutehammond.pill.domain.pill.presentation.api;

import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.domain.pill.application.PillService;
import me.cutehammond.pill.domain.pill.domain.PillElementUpdateType;
import me.cutehammond.pill.domain.pill.domain.dto.*;
import me.cutehammond.pill.global.common.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pill")
@RequiredArgsConstructor
public class PillController {

    private final PillService pillService;

    @PostMapping
    public ApiResponse<Long> createPill(@RequestBody PillCreateRequest createRequest, @RequestBody PillElementRequest elementRequest) {
        // pillSequence (= id)
        long id = pillService.createPill(createRequest, elementRequest);

        return ApiResponse.success("id", id);
    }

    @PutMapping("/{type}")
    public ApiResponse<Long> updatePill(@RequestBody PillUpdateRequest updateRequest, @RequestBody PillElementRequest elementRequest, @PathVariable String type) {
        // pillSequence (= id)
        long id = pillService.update(updateRequest);

        PillElementUpdateType updateType = PillElementUpdateType.valueOf(type);
        pillService.updatePillElement(elementRequest, updateType);

        return ApiResponse.success("id", id);
    }

    @GetMapping("/{id}")
    public ApiResponse<PillResponse> find(@PathVariable Long id) {
        PillResponse response = pillService.getPill(id);

        return ApiResponse.success("response", response);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<PillResponse> delete(@PathVariable Long id) {
        PillResponse response = pillService.getPill(id);
        pillService.deletePill(new PillDeleteRequest(id));

        return ApiResponse.success("response", response);
    }

}
