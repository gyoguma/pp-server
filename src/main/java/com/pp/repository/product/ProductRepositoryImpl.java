package com.pp.repository.product;

import com.pp.domain.QCategory;
import com.pp.domain.QMember;
import com.pp.domain.QProduct;
import com.pp.web.dto.product.ProductResponseDTO;
import com.pp.web.dto.product.QProductResponseDTO_ShowProductDTO;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ProductResponseDTO.ShowProductDTO> getSearchProductList(String keyword, Pageable pageable) {
        QMember member = QMember.member;
        QProduct product = QProduct.product;
        QCategory category = QCategory.category;

        List<ProductResponseDTO.ShowProductDTO> result = queryFactory
                .select(new QProductResponseDTO_ShowProductDTO(
                        product.title,
                        member.nickname,
                        product.price,
                        product.id,
                        category.id,
                        product.status,
                        product.createdAt,
                        product.updatedAt))
                .from(product)
                .join(product.member, member)
                .join(product.category, category)
                .where(product.title.like("%" + keyword + "%"))
                .offset(pageable.getOffset())  // 페이징 처리
                .limit(pageable.getPageSize()) // 페이징 처리
                .fetch();

        // 총 개수 계산
        long total = queryFactory
                .select(product.count())
                .from(product)
                .where(product.title.like("%" + keyword + "%"))
                .fetchOne();

        // Page 객체로 변환하여 반환
        return new PageImpl<>(result, pageable, total);
    }
}
