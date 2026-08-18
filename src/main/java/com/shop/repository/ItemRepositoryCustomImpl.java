package com.shop.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.dto.QMainItemDto;
import com.shop.entity.Item;
import com.shop.entity.ItemImg;
import com.shop.entity.QItem;
import com.shop.entity.QItemImg;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {
    private JPAQueryFactory queryFactory;


    public ItemRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus) {
        return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
    }

    private BooleanExpression regDtsAfter(String searchDateType) {
        LocalDateTime dateTime = LocalDateTime.now();

        if (StringUtils.equals("all", searchDateType) || searchDateType == null) {
            return null;
        } else if (StringUtils.equals("1d", searchDateType)) {
            dateTime = dateTime.minusDays(1);
        } else if (StringUtils.equals("1w", searchDateType)) {
            dateTime = dateTime.minusWeeks(1);
        } else if (StringUtils.equals("1m", searchDateType)) {
            dateTime = dateTime.minusMonths(1);
        } else if (StringUtils.equals("6m", searchDateType)) {
            dateTime = dateTime.minusMonths(6);
        }
        return QItem.item.regTime.after(dateTime);
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery) {
        if (StringUtils.equals("itemNm", searchBy)) {
            return QItem.item.itemNm.like("%" + searchQuery + "%");
        } else if (StringUtils.equals("createdBy", searchBy)) {
            return QItem.item.createdBy.like("%" + searchQuery + "%");
        }
        return null;
    }

    // ================= [신규 추가] 가격 조건 메소드 =================
    // maxPrice 이하(Less Than or Equal)인 상품만 조회
    private BooleanExpression priceLoe(Integer maxPrice) {
        return maxPrice == null ? null : QItem.item.price.loe(maxPrice);
    }

    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        List<Item> content = queryFactory
                .selectFrom(QItem.item)
                .where(
                        regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery())
                )
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 개수 조회 (fetchCount 최신 방식)
        Long total = queryFactory
                .select(QItem.item.count())
                .from(QItem.item)
                .where(
                        regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    private BooleanExpression itemNmLike(String searchQuery) {
        return StringUtils.isEmpty(searchQuery) ? null : QItem.item.itemNm.like("%" + searchQuery + "%");
    }
    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        // 1. 메인 페이지 상품 리스트 조회
        List<MainItemDto> content = queryFactory
                .select(new QMainItemDto(
                        item.id,
                        item.itemNm,
                        item.itemDetail,
                        itemImg.imgUrl,
                        item.price,
                        item.isNew

                ))
                .from(itemImg)
                .join(itemImg.item, item)
                .where(
                        itemImg.repImgYn.eq("Y"),
                        itemNmLike(itemSearchDto.getSearchQuery()),
                        priceLoe(itemSearchDto.getMaxPrice()),
                        isNewEq(itemSearchDto.getIsNew()) // 👈 신상품 조건 추가
                )
                .orderBy(getPriceOrder(itemSearchDto.getSortPrice()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 2. 조건에 맞는 전체 데이터 개수 조회
        Long total = queryFactory
                .select(itemImg.count())
                .from(itemImg)
                .join(itemImg.item, item)
                .where(
                        itemImg.repImgYn.eq("Y"),
                        itemNmLike(itemSearchDto.getSearchQuery()),
                        priceLoe(itemSearchDto.getMaxPrice()),
                         isNewEq(itemSearchDto.getIsNew())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    // 라디오 버튼 선택값("desc", "asc")에 따라 OrderSpecifier를 반환하는 메서드
    private OrderSpecifier<?> getPriceOrder(String sortPrice) {
        if ("desc".equalsIgnoreCase(sortPrice)) {
            return QItem.item.price.desc(); // 높은 가격순
        }
        return QItem.item.price.asc();     // 낮은 가격순 (기본값)
    }

    // 신상품 체크가 true일 때만 isNew == true 조건 반환
    private BooleanExpression isNewEq(Boolean isNew) {
        // null 체크를 빼고 바로 비교하거나 Boolean으로 받기
        return (isNew != null && isNew) ? QItem.item.isNew.eq(true) : null;
    }
}