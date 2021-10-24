package com.zst.week11.q1.module.order.controller;

import com.zst.week11.q1.module.order.dto.OrderDTO;
import com.zst.week11.q1.module.order.dto.OrderDTOMapper;
import com.zst.week11.q1.module.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private ApplicationContext applicationContext;

    @GetMapping("/get")
    public OrderDTO get(@RequestParam long id) {
        return OrderDTOMapper.mapToDTO(orderService.get(id));
    }
}
