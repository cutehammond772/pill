package me.cutehammond.pill.domain.point.application;

import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.domain.point.domain.PillPoint;
import me.cutehammond.pill.domain.point.domain.PillPointContainer;
import me.cutehammond.pill.domain.point.domain.PillPointOrder;
import me.cutehammond.pill.domain.point.domain.PillPointSpec;
import me.cutehammond.pill.domain.point.domain.dao.sql.PillPointRepository;
import me.cutehammond.pill.domain.point.domain.dao.sql.PillPointSpecRepository;
import me.cutehammond.pill.domain.point.domain.dto.request.PillPointSpecRequest;
import me.cutehammond.pill.domain.point.domain.dto.response.PillPointResponse;
import me.cutehammond.pill.domain.point.exception.particular.PillPointSpecNotFoundException;
import me.cutehammond.pill.domain.user.application.UserService;
import me.cutehammond.pill.domain.user.domain.dao.sql.UserRepository;
import me.cutehammond.pill.domain.user.exception.UserNotFoundException;
import me.cutehammond.pill.domain.user.exception.UserUnauthorizedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PillPointService {

    private final UserRepository userRepository;
    private final UserService userService;

    private final PillPointSpecRepository pillPointSpecRepository;
    private final PillPointRepository pillPointRepository;

    @Transactional
    public void addPointSpec(PillPointSpecRequest pillPointSpecRequest) {
        PillPointSpec pillPointSpec = new PillPointSpec(pillPointSpecRequest);
        pillPointSpecRepository.save(pillPointSpec);
    }

    @Transactional(readOnly = true)
    public int getPointsLeft(Optional<String> userId) {
        return fetchPillPointContainer(userId.orElse(fetchCurrentUserId())).getPointsLeft();
    }

    @Transactional
    public void addPoint(Optional<String> userId, long specId) {
        PillPoint pillPoint = fetchPillPointContainer(userId.orElse(fetchCurrentUserId()))
                .addPoint(fetchPillPointSpec(specId));
        pillPointRepository.save(pillPoint);
    }

    @Transactional
    public void usePoints(Optional<String> userId, int point) {
        fetchPillPointContainer(userId.orElse(fetchCurrentUserId())).usePoints(point);
    }

    @Transactional(readOnly = true)
    public List<PillPointResponse> getPoints(Optional<String> userId,
                                             boolean includeRunOut, boolean includeExpired, PillPointOrder... orders) {
        return fetchPillPointContainer(userId.orElse(fetchCurrentUserId()))
                .getPoints(includeRunOut, includeExpired, orders).stream()
                .map(PillPointResponse::from)
                .toList();
    }

    @Transactional(propagation = Propagation.MANDATORY)
    String fetchCurrentUserId() {
        return userService.getCurrentUser()
                .orElseThrow(UserUnauthorizedException::new)
                .getUserId();
    }

    @Transactional(propagation = Propagation.MANDATORY)
    PillPointContainer fetchPillPointContainer(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException(userId))
                .getPillPointContainer();
    }

    @Transactional(propagation = Propagation.MANDATORY)
    PillPointSpec fetchPillPointSpec(long specId) {
        return pillPointSpecRepository.findById(specId)
                .orElseThrow(() -> new PillPointSpecNotFoundException(Long.toString(specId)));
    }

}
