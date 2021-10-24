package com.zst.week11.q1.module.order.service;

import com.zst.week11.q1.module.order.dao.OrderDao;
import com.zst.week11.q1.module.order.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    @Autowired
    private OrderDao orderDao;

    @Cacheable(cacheNames = "order")
    public Order get(long id) {
        return orderDao.get(id);
    }
}
