package com.pp.service.member;

import com.pp.domain.Member;
import com.pp.domain.Product;
import org.springframework.data.domain.Page;

public interface MemberQueryService { // 조회

    Member getMember(Long memberId); // 단일 회원 조회

    Page<Product> getMemberProductList(Member member, Integer page);
}
