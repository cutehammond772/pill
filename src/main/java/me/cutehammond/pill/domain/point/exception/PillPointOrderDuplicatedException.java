package me.cutehammond.pill.domain.point.exception;

import lombok.Getter;
import lombok.NonNull;
import me.cutehammond.pill.domain.point.domain.PillPointOrder;
import me.cutehammond.pill.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PillPointOrderDuplicatedException extends PillPointException {

    private final List<PillPointOrder> duplicate;

    public PillPointOrderDuplicatedException(@NonNull Collection<PillPointOrder> duplicate) {
        super("포인트 정렬 순서에 중복된 기준이 있습니다. [" +
                duplicate.stream().map(PillPointOrder::name).collect(Collectors.joining(", ")) +
                "]", ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST);

        this.duplicate = duplicate.stream().toList();
    }

}
