package com.pp.service.member;

import com.pp.domain.Member;
import com.pp.domain.Product;
import com.pp.repository.member.MemberRepository;
import com.pp.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberQueryServiceImpl implements MemberQueryService {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    @Override
    public Member getMember(Long memberId) {
        return memberRepository.findById(memberId).get();
    }

    @Override
    public Page<Product> getMemberProductList(Member member, Integer page) {
        return productRepository.findAllByMember(member, PageRequest.of(page, 10));
    }
}
