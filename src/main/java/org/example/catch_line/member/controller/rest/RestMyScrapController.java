package org.example.catch_line.member.controller.rest;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.catch_line.common.constant.SessionConst;
import org.example.catch_line.member.service.MyScrapService;
import org.example.catch_line.restaurant.model.dto.RestaurantResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/my-page/scraps")
public class RestMyScrapController {

    private final MyScrapService myScrapService;

    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> findMyRestaurantsByScrap(
            HttpSession httpSession
    ) {
        List<RestaurantResponse> myRestaurants = myScrapService.findMyRestaurants((Long) httpSession.getAttribute(SessionConst.MEMBER_ID));
        return ResponseEntity.ok().body(myRestaurants);
    }


}
