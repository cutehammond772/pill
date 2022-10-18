package me.cutehammond.pill.domain.point.domain;

import lombok.*;
import me.cutehammond.pill.domain.point.exception.PillPointAccumulationFailedException;
import me.cutehammond.pill.domain.point.exception.PillPointOutOfBoundsException;
import me.cutehammond.pill.domain.point.exception.particular.PillPointExpiredException;
import me.cutehammond.pill.domain.point.exception.PillPointOrderDuplicatedException;
import me.cutehammond.pill.domain.point.exception.particular.PillPointUsingFailedException;
import me.cutehammond.pill.domain.user.domain.User;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "PILL_POINT_CONTAINERS_TABLE")
public class PillPointContainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Getter
    @OneToOne(mappedBy = "pillPointContainer", fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "pillPointContainer", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<PillPoint> points = new ArrayList<>();

    public PillPointContainer(@NonNull User user) {
        this.user = user;
    }

    /**
     * 포인트 명세에 따라 발행 후 적립합니다.
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public PillPoint addPoint(@NonNull PillPointSpec spec) {
        if (spec.isExpired())
            throw new PillPointExpiredException(spec.getName());

        PillPoint pillPoint = new PillPoint(spec, this);

        points.add(pillPoint);
        return pillPoint;
    }

    /**
     * 현재 '사용 가능한' 포인트의 수량을 반환합니다.
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public int getPointsLeft() {
        return points.stream()
                .filter(p -> !p.isRunOut() && !p.isExpired())
                .map(PillPoint::getPointLeft)
                .reduce(Integer::sum)
                .orElseThrow(PillPointAccumulationFailedException::new);
    }

    /**
     * 특정 조건에 따라 현재 포인트의 기록을 반환합니다. <br>
     * 포인트 정렬 순서의 경우, <br>
     * 1. 앞에 있는 PillPointOrder 기준부터 우선됩니다. <br>
     * 2. 같은 시그니처(=특정 기준)의 PillPointOrder를 동시에 사용할 수 없습니다. <br>
     * ex. getPoints(..., CLOSE_TO_EXPIRATION, FEW_POINTS_LEFT_FIRST)의 경우 <br>
     * 포인트 만료 날짜가 가까운 것이 1순위, 얼마 안남은 포인트가 2순위로 순서가 결정됩니다.
     * @param includeRunOut 모두 사용된 포인트의 기록을 포함할 지 결정합니다.
     * @param includeExpired 현재 만료된 포인트의 기록을 포함할 지 결정합니다.
     * @param orders 포인트 기록의 정렬 기준을 나타냅니다.
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public List<PillPoint> getPoints(boolean includeRunOut, boolean includeExpired, @NonNull PillPointOrder... orders) {
        /* 유효성 검증: 같은 기준을 동시에 대입하면 예외 발생 */
        validateOrders(orders);

        /* 맨 뒤에 있는 정렬 기준부터 정렬해야 한다. */
        var reversedOrders = Arrays.asList(orders);
        Collections.reverse(reversedOrders);

        for (PillPointOrder order : reversedOrders)
            points.sort(order.getComparator());

        return points.stream()
                .filter(p -> includeExpired || !p.isExpired())
                .filter(p -> includeRunOut || !p.isRunOut())
                .collect(Collectors.toList());
    }

    /**
     * 포인트 사용 순서에 따라 포인트를 사용합니다. <br><br>
     * 포인트 사용 순서의 경우, <br>
     * 1. 앞에 있는 PillPointOrder 기준부터 우선됩니다. <br>
     * 2. 같은 시그니처(=특정 기준)의 PillPointOrder를 동시에 사용할 수 없습니다. <br><br>
     * ex. usePoints(..., CLOSE_TO_EXPIRATION, FEW_POINTS_LEFT_FIRST)의 경우 <br>
     * 포인트 만료 날짜가 가까운 것이 1순위, 얼마 안남은 포인트가 2순위로 순서가 결정됩니다.
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void usePoints(int point, @NonNull PillPointOrder... orders) {
        /* 사용하는 포인트가 0 이하일 때 */
        if (point <= 0)
            throw new PillPointOutOfBoundsException("사용할 포인트가 0 이하입니다.");

        int pointsLeft = getPointsLeft();

        /* 사용 가능한 남은 포인트보다 사용하는 포인트가 더 클 때 */
        if (pointsLeft < point)
            throw new PillPointOutOfBoundsException("사용 가능한 남은 포인트보다 사용하는 포인트가 더 큽니다.");

        /* 사용 가능한 포인트 리스트 */
        List<PillPoint> pointsList = getPoints(false, false, orders);

        for (PillPoint pillPoint : pointsList) {
            if (pillPoint.getPointLeft() >= point) {
                pillPoint.usePoint(point);
                break;
            }

            /* 모두 사용된 포인트 상태가 됨 */
            pillPoint.usePoint(pillPoint.getPointLeft());
            point -= pillPoint.getPointLeft();
        }
    }

    private void validateOrders(@NonNull PillPointOrder... orders) {
        try {
            var groups = Arrays.stream(orders)
                    .collect(Collectors.groupingBy(PillPointOrder::getAttr));

            var duplicates = groups.keySet().stream()
                    .filter(attr -> groups.get(attr).size() > 1).toList();

            if (duplicates.size() > 0)
                throw new PillPointOrderDuplicatedException(
                        duplicates.stream().map(groups::get).flatMap(List::stream).toList()
                );
        } catch (NullPointerException e) {
          throw new PillPointAccumulationFailedException("정렬 순서 중 null 값이 존재합니다.");
        }
    }

}
