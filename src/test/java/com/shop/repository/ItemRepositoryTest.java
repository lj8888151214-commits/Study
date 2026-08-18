package com.shop.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import com.shop.entity.QItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest // 스프링 부트 테스트다
// 테스트 설정을 application-test.properties 세팅한다
@TestPropertySource(locations = "classpath:application-test.properties")
class ItemRepositoryTest {
    // 객체를 연결한다 자동으로
    // 스프링 객체 지향 디자인 패턴 -> 1.싱글톤 2.빌더패턴
    // 싱글톤
    // 스프링 컨테이너가 있습니다. -> 객체를 관리하는데
    // 컨테이너에서 꺼내서 줍니다. 무슨 객체를 ItemRepository 객체를 -> 무조건 1개
    @Autowired
    ItemRepository itemRepository; // 너무 쉽죠 너무 쉽죠

    @PersistenceContext //영속성 컨텍스트 -> EntityManager 객체를 받습니다.
    EntityManager em;

    @Test // 테스트다
    @DisplayName("상품 저장 테스트") // 테스트 명
    public void createItemTest(){
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        Item savedItem = itemRepository.save(item); // 너무 쉽죠
        System.out.println(savedItem.toString());
    }

    public void createItemList(){
        for(int i = 1; i<=10;i++){
            Item item = new Item();
            item.setItemNm("테스트 상품"+i);
            item.setPrice(10000+i);
            item.setItemDetail("테스트 상품 상세 설명"+i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            Item savedItem = itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("상품면 조회 테스트")
    public void findByItemNmTest(){
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemNm("테스트 상품1");
        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("상품명, 상품상세설명 or 테스트")
    public void findByItemNmOrItemDetailTest(){
        this.createItemList();
        List<Item> itemList = itemRepository
                .findByItemNmOrItemDetail("테스트 상품1","테스트 상품 상세 설명5");
        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("가격 LessThan 테스트")
    public void findByPriceLessThanTest(){
        this.createItemList();
        List<Item> itemList = itemRepository
                .findByPriceLessThan(10005);
        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("가격 내림차순 조회 테스트")
    public void findByPriceLessThanOrderByPriceDescTest(){
        this.createItemList();
        List<Item> itemList = itemRepository
                .findByPriceLessThanOrderByPriceDesc(10005);
        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("@Query를 이용한 상품 조회 테스트")
    public void findByItemDetailTest(){
        this.createItemList();
        List<Item> itemList = itemRepository
                .findByItemDetail("테스트 상품 상세 설명");
        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }
    @Test
    @DisplayName("nativeQuery 속성을 이용한 상품 조회 테스트")
    public void findByItemDetailNativeTest(){
        this.createItemList();
        List<Item> itemList = itemRepository
                .findByItemDetailNative("테스트 상품 상세 설명");
        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("Querydsl 조회테스트1") // 제발 공부하세요 주말에 놀지말고 1시간만이라도
    public void queryDslTest(){
        this.createItemList(); // 10개 데이터 DB에 어장
        // JPAQueryFactory 객체를 생성하는 -> 생성자 매개변수 EntityManager
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        // QItem?? -> targer/generated-sources/java...QItem
        QItem qItem = QItem.item;
        //                    EntityManager를 이용한 JPAQueryFactory를 이용해서
        //                    쿼리문을 만듭니다. 빌더패턴
        // select * from item where itemSellStuats = SELL and itemDetail like %테스트 상품 상세 설명% order by price desc
        JPAQuery<Item> query = queryFactory.selectFrom(qItem)
                .where(qItem.itemSellStatus.eq(ItemSellStatus.SELL))
                .where(qItem.itemDetail.like("%"+"테스트 상품 상세 설명" + "%"))
                .orderBy(qItem.price.desc());

        List<Item> itemList = query.fetch();
        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }

    public void createItemList2(){
        for(int i = 1;i<=5;i++){
            Item item = new Item();
            item.setItemNm("테스트 상품"+i);
            item.setPrice(10000+i);
            item.setItemDetail("테스트 상품 상세 설명"+i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }
        for(int i = 6;i<=10;i++){
            Item item = new Item();
            item.setItemNm("테스트 상품"+i);
            item.setPrice(10000+i);
            item.setItemDetail("테스트 상품 상세 설명"+i);
            item.setItemSellStatus(ItemSellStatus.SOLD_OUT);
            item.setStockNumber(0);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }
    }
    @Test
    @DisplayName("상품 Querydsl 조회테스트 2")
    public void queryDslTest2(){
        // 1~5 SELL 6~10 SOLD_OUT 데이터가 H2 데이터 베이스에 들어갑니다.
        this.createItemList2();
        // BooleanBuilder QueryDsl 쿼리를 만들어 사용 할 수 있는 놈
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        //item 가져왔습니다.
        QItem item = QItem.item;
        // 검색할 데이터를 변수 저장
        String itemDetail = "테스트 상품 상세 설명";
        int price = 10003;
        String itemSellStat = "SELL";
        //where 절 itemDetail = "테스트 상품 상세 설명" and price > 10003
        booleanBuilder.and(item.itemDetail.like("%"+itemDetail+"%"));
        booleanBuilder.and(item.price.gt(price));

        //item 상태가 SELL이 있으면 true
        if(StringUtils.equals(itemSellStat, ItemSellStatus.SELL)){
            // and itemSellStauts = "SELL"
            booleanBuilder.and(item.itemSellStatus.eq(ItemSellStatus.SELL));
        }
        //중요 [1][2][3] -> Pageable 시작 0 5개만 빼
        Pageable pageable = PageRequest.of(0,5);
        // DB 조건 확인 후에 Pageable 형식으로 세팅 -> Page<Item>
        Page<Item> itemPagingResult =
                itemRepository.findAll(booleanBuilder,pageable);


        System.out.println("total elements : "+
                itemPagingResult.getTotalElements());

        List<Item> resultItemList = itemPagingResult.getContent();
        for(Item resultItem : resultItemList){
            System.out.println(resultItem.toString());
        }
    }

}