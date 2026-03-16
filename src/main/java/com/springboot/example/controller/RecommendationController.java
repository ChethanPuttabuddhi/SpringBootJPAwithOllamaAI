package com.springboot.example.controller;

import com.springboot.example.dto.Order;
import com.springboot.example.service.AiRecommendationService;
import com.springboot.example.dto.RecommendationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RecommendationController {

    @Autowired
    private AiRecommendationService aiRecommendationService;

    @PostMapping("/recommendations")
    public RecommendationResponse recommendNextProducts(
            @RequestParam(required = false, defaultValue = "anonymous") Long userId) {

        return aiRecommendationService.recommendForOrder(userId);
    }
}