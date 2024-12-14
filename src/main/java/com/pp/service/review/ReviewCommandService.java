package com.pp.service.review;

import com.pp.domain.Member;

public interface ReviewCommandService {

    void changeRating(Member member, Double starRating);
}
