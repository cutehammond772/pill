package me.cutehammond.pill.domain.pill.api;

import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.domain.pill.application.PillService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pill")
@RequiredArgsConstructor
public class PillController {

    private final PillService pillService;

}
