package com.shop.service;

import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemFormDto;
import com.shop.entity.Item;
import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class ItemServiceTest {
    @Autowired
    ItemService itemService;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ItemImgRepository itemImgRepository;

    List<MultipartFile> createMultipartFiles() throws  Exception{
        // MultipartFile -> 비정형데이터 이미지 파일
        // 이미지 파일 List
        List<MultipartFile> multipartFileList = new ArrayList<>();
        // 5반복
        for(int i = 0;i<5;i++){
            String path = "C:/shop/item/"; // 경로를 정의 합니다. 문자열 변수
            String imageName = "image" + i +".jpg"; // 파일명을 만들어 줍니다. image0.jpg..image4.jpg
            //??--------------
            // MockMultipartFile -> 가상 MultipartFile
            MockMultipartFile mockMultipartFile =
                    new MockMultipartFile(path, imageName,
                            "image/jpg",new byte[]{1,2,3,4});

            multipartFileList.add(mockMultipartFile);
        }
        return multipartFileList;
    }

    @Test
    @DisplayName("상품 등록 테스트")
    void saveItem() throws Exception{
        ItemFormDto itemFormDto = new ItemFormDto();
        itemFormDto.setItemNm("테스트상품");
        itemFormDto.setItemSellStatus(ItemSellStatus.SELL);
        itemFormDto.setItemDetail("테스트 상품 입니다.");
        itemFormDto.setPrice(1000);
        itemFormDto.setStockNumber(100);
        // 이미지 리스트 생성 됐다
        List<MultipartFile> multipartFileList = createMultipartFiles();
        // itemService -> save 진행
        // ItemFormDto -> 아이템정보
        // multipartFileList -> 아이템 이미지 리스트
        Long itemId = itemService.saveItem(itemFormDto, multipartFileList);
        // itemImgRepository -> ItemImg 리스트를 받습니다. findByItemIdOrderByIdAsc(itemId)
        List<ItemImg> itemImgList =
                itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        // itemRepository -> Item 추출
        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);
        // 확인작업
        assertEquals(itemFormDto.getItemNm(), item.getItemNm());
        assertEquals(itemFormDto.getItemSellStatus(), item.getItemSellStatus());
        assertEquals(itemFormDto.getItemDetail(),item.getItemDetail());
        assertEquals(itemFormDto.getPrice(), item.getPrice());
        assertEquals(itemFormDto.getStockNumber(),item.getStockNumber());
        assertEquals(multipartFileList.get(0).getOriginalFilename(),itemImgList.get(0).getOriImgName());
    }
}