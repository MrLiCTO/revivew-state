package com.wxcs.order.revivew.listener;

import com.wxcs.order.revivew.config.OrderStateMachineConfig;
import com.wxcs.order.revivew.enums.OrderStatus;
import com.wxcs.order.revivew.enums.OrderStatusChangeEvent;
import com.wxcs.order.revivew.model.Order;
import org.springframework.messaging.Message;
import org.springframework.statemachine.annotation.OnTransition;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.stereotype.Component;

@Component("orderStateListener")
@WithStateMachine(id = OrderStateMachineConfig.orderStateMachineId)
//@WithStateMachine
public class OrderStateListener {

    @OnTransition(source = "WAIT_PAYMENT", target = "WAIT_DELIVER")
    public boolean payTransition(Message<OrderStatusChangeEvent> message) {
        Order order = (Order) message.getHeaders().get("order");
        order.setStatus(OrderStatus.WAIT_DELIVER);
        System.out.println("支付 headers=" + message.getHeaders().toString());
        return true;
    }

    @OnTransition(source = "WAIT_PAYMENT", target = "CLOSED")
    public boolean cancelTransition(Message<OrderStatusChangeEvent> message) {
        Order order = (Order) message.getHeaders().get("order");
        order.setStatus(OrderStatus.CLOSED);
        System.out.println("支付 headers=" + message.getHeaders().toString());
        return true;
    }

    @OnTransition(source = "WAIT_DELIVER", target = "WAIT_RECEIVE")
    public boolean deliverTransition(Message<OrderStatusChangeEvent> message) {
        Order order = (Order) message.getHeaders().get("order");
        order.setStatus(OrderStatus.WAIT_RECEIVE);
        System.out.println("发货 headers=" + message.getHeaders().toString());
        return true;
    }

    @OnTransition(source = "WAIT_RECEIVE", target = "FINISH")
    public boolean receiveTransition(Message<OrderStatusChangeEvent> message) {
        Order order = (Order) message.getHeaders().get("order");
        order.setStatus(OrderStatus.FINISH);
        System.out.println("收货 headers=" + message.getHeaders().toString());
        return true;
    }

    @OnTransition(source = "FINISH", target = "WAIT_RETURN")
    public boolean returnTransition(Message<OrderStatusChangeEvent> message) {
        Order order = (Order) message.getHeaders().get("order");
        order.setStatus(OrderStatus.WAIT_RETURN);
        System.out.println("收货 headers=" + message.getHeaders().toString());
        return true;
    }

    @OnTransition(source = {"WAIT_RECEIVE","WAIT_DELIVER","WAIT_RETURN"}, target = "RETURN")
    public boolean refundTransition(Message<OrderStatusChangeEvent> message) {
        Order order = (Order) message.getHeaders().get("order");
        order.setStatus(OrderStatus.RETURN);
        System.out.println("收货 headers=" + message.getHeaders().toString());
        return true;
    }
}