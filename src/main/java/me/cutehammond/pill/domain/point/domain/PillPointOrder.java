package me.cutehammond.pill.domain.point.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;

import static java.util.Comparator.*;

@Getter
@RequiredArgsConstructor
public enum PillPointOrder {

    /**
     * 만료 날짜가 가까운 순으로 사용됩니다.
     */
    CLOSE_TO_EXPIRATION(PointAttr.EXPIRATION_DATE,
            comparing(p -> p.getSpec().getExpirationDate())),

    /**
     * 만료 날짜가 먼 순으로 사용됩니다.
     */
    FAR_TO_EXPIRATION(PointAttr.EXPIRATION_DATE,
            comparing(p -> p.getSpec().getExpirationDate(), Comparator.reverseOrder())),

    /**
     * 최근에 적립된 순으로 사용됩니다.
     */
    CLOSE_TO_RECEIVED(PointAttr.RECEIVED_DATE,
            comparing(PillPoint::getReceivedDate)),

    /**
     * 오래 전에 적립된 순으로 사용됩니다.
     */
    FAR_TO_RECEIVED(PointAttr.RECEIVED_DATE,
            comparing(PillPoint::getReceivedDate, Comparator.reverseOrder())),

    /**
     * 특정 이름으로 적립된 포인트가 얼마 남지 않았을 때 이를 먼저 사용합니다.
     */
    FEW_POINT_LEFT_FIRST(PointAttr.AMOUNT_OF_POINT,
            comparing(PillPoint::getPointLeft)),

    /**
     * 특정 이름으로 적립된 포인트가 많이 남았을 때 이를 먼저 사용합니다.
     */
    LOTS_OF_POINT_LEFT_FIRST(PointAttr.AMOUNT_OF_POINT,
            comparing(PillPoint::getPointLeft, Comparator.reverseOrder()));

    private final PointAttr signature;
    private final Comparator<PillPoint> comparator;

    public enum PointAttr {
        EXPIRATION_DATE, RECEIVED_DATE, AMOUNT_OF_POINT;
    }
}
