package me.cutehammond.pill.domain.pill.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.cutehammond.pill.domain.pill.domain.dao.sql.PillRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PillService {

    private final PillRepository pillRepository;

}
