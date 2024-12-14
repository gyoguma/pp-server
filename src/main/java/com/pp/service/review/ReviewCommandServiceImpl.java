package com.pp.service.review;

import com.pp.domain.Member;
import com.pp.repository.review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReviewCommandServiceImpl implements ReviewCommandService {

    private final ReviewRepository reviewRepository;

    @Override
    @Transactional
    public void changeRating(Member member, Double starRating) {
        // starRating 값에 따라 changeRating 값을 설정
        Map<Double, Double> ratingMap = Map.of(
                5.0, 0.05,
                4.0, 0.03,
                3.0, 0.00,
                2.0, -0.03,
                1.0, -0.05
        );

        // 기본값은 0.00으로 설정하고 매칭되는 값이 있으면 해당 값 반환
        Double changeRating = ratingMap.getOrDefault(starRating, 0.00);

        member.setMemberRating(changeRating);
    }
}
