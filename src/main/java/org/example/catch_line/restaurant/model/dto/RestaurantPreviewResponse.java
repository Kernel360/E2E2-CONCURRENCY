package org.example.catch_line.restaurant.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.catch_line.restaurant.model.entity.constant.FoodType;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Builder
public class RestaurantPreviewResponse {

    private Long restaurantId;
    private String name;
    private BigDecimal averageRating;
    private Long reviewCount;
    private String foodType;
    private String serviceType;

}
