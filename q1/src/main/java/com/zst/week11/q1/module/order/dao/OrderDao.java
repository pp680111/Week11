package com.zst.week11.q1.module.order.dao;


import com.zst.week11.q1.module.order.entity.Order;

import java.util.List;

public interface OrderDao {
    Order get(long id);

    Order getFirst();

    long count();

    List<Order> page(int start, int end);
}
