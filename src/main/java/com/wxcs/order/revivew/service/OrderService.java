package com.wxcs.order.revivew.service;

import com.wxcs.order.revivew.enums.OrderStatus;
import com.wxcs.order.revivew.enums.OrderStatusChangeEvent;
import com.wxcs.order.revivew.handler.OrderStateHandler;
import com.wxcs.order.revivew.model.Order;
import com.wxcs.order.revivew.po.PageInfo;
import com.wxcs.order.revivew.repository.OrderRepository;
import com.wxcs.order.revivew.utils.UuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderStateHandler orderStateHandler;

    public PageInfo<Order> list(Integer pageNo, Integer pageSize) throws Exception{
        Page<Order> page = orderRepository.findAll(PageRequest.of(0, pageSize, Sort.by(Sort.Direction.DESC, "orderNo")));
        if (page != null)
            return new PageInfo<Order>(page.getContent(), page.getTotalPages());
        return null;
    }

    public void delete(String id) throws Exception{
        orderRepository.deleteById(id);
    }

    public void create(Order order) throws Exception{
        order.setId(UuidUtil.get32UUID());
        order.setDesc("测试");
        order.setOrderNo(System.currentTimeMillis() / 1000 + "");
        orderStateHandler.createOrder(order);
    }

    public void receive(String id) throws Exception{
        Order order = orderRepository.findById(id).get();
        if (!OrderStatus.WAIT_RECEIVE.name().equals(order.getStatus().name()))
            return;
        Message<OrderStatusChangeEvent> message = MessageBuilder.withPayload(OrderStatusChangeEvent.RECEIVED).setHeader("order", order).build();
        orderStateHandler.sendEvent(message, order);
    }

    public void deliver(String id) throws Exception{
        Order order = orderRepository.findById(id).get();
        if (!OrderStatus.WAIT_DELIVER.name().equals(order.getStatus().name()))
            return;
        Message<OrderStatusChangeEvent> message = MessageBuilder.withPayload(OrderStatusChangeEvent.DELIVERY).setHeader("order", order).build();
        orderStateHandler.sendEvent(message, order);
    }

    public void pay(String id) throws Exception{
        Order order = orderRepository.findById(id).get();
        if (!OrderStatus.WAIT_PAYMENT.name().equals(order.getStatus().name()))
            return;
        Message<OrderStatusChangeEvent> message = MessageBuilder.withPayload(OrderStatusChangeEvent.PAYED).setHeader("order", order).build();
        orderStateHandler.sendEvent(message, order);
    }

    public void cancel(String id) throws Exception{
        Order order = orderRepository.findById(id).get();
        if (!OrderStatus.WAIT_PAYMENT.name().equals(order.getStatus().name()))
            return;
        Message<OrderStatusChangeEvent> message = MessageBuilder.withPayload(OrderStatusChangeEvent.CANCEL).setHeader("order", order).build();
        orderStateHandler.sendEvent(message, order);
    }

    public void refund(String id) throws Exception{
        Order order = orderRepository.findById(id).get();
        //TODO Arrays.asList(OrderStatus.values()).containsAll();
        if ((!OrderStatus.WAIT_RECEIVE.name().equals(order.getStatus().name()))&&(!OrderStatus.WAIT_DELIVER.name().equals(order.getStatus().name())))
            return;
        Message<OrderStatusChangeEvent> message = MessageBuilder.withPayload(OrderStatusChangeEvent.REFUND).setHeader("order", order).build();
        orderStateHandler.sendEvent(message, order);
    }

    public void returng(String id) throws Exception{
        Order order = orderRepository.findById(id).get();
        if (!OrderStatus.FINISH.name().equals(order.getStatus().name()))
            return;
        Message<OrderStatusChangeEvent> message = MessageBuilder.withPayload(OrderStatusChangeEvent.RETURN).setHeader("order", order).build();
        orderStateHandler.sendEvent(message, order);
    }

    public void receiveReturn(String id) throws Exception{
        Order order = orderRepository.findById(id).get();
        if (!OrderStatus.WAIT_RETURN.name().equals(order.getStatus().name()))
            return;
        Message<OrderStatusChangeEvent> message = MessageBuilder.withPayload(OrderStatusChangeEvent.RECEIVERETURN).setHeader("order", order).build();
        orderStateHandler.sendEvent(message, order);
    }

}
