package com.wxcs.order.revivew.repository;

import com.wxcs.order.revivew.model.Order;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface OrderRepository extends PagingAndSortingRepository<Order, String> {
}
