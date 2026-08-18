package com.shop.repository;

import com.shop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item,Long>, QuerydslPredicateExecutor<Item>,
                                        ItemRepositoryCustom{
    // select * from item where itemNm = itemNm;
    List<Item> findByItemNm(String itemNm);
    // select * from item where itemNm = itemNm or itemDetail = itemDetail;
    List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail);
    // select * from item where price < price;
    List<Item> findByPriceLessThan(Integer price);
    // select * from item where price < price order by price desc;
    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);
    // select * from item where itemDetail like %itemDetail% order by price desc;
    @Query("select i from Item i where i.itemDetail like %:itemDetail% order by i.price desc")
    List<Item> findByItemDetail(@Param("itemDetail") String itemDetail);

    @Query(value = "select * from item i where i.item_detail like %:itemDetail% order by i.price desc",
    nativeQuery = true)
    List<Item> findByItemDetailNative(@Param("itemDetail") String itemDetail);

}
