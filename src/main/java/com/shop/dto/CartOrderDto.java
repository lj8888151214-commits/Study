package com.shop.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Getter
@Setter
public class CartOrderDto {
    private  Long cartItemId;

    private List<CartOrderDto> cartOrderDtoList;

}
