package com.pp.domain;

import com.pp.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@DynamicUpdate
@DynamicInsert
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Location extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    String name; // 거래 장소 이름

    @Column(columnDefinition = "TEXT")
    String locationPictureUrl; // 거래 장소 사진

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL)
    private List<Product> productList = new ArrayList<>();
}
