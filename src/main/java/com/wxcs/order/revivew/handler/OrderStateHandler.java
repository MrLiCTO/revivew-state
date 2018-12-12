package com.wxcs.order.revivew.handler;

import com.wxcs.order.revivew.config.OrderStateMachineConfig;
import com.wxcs.order.revivew.enums.OrderStatus;
import com.wxcs.order.revivew.enums.OrderStatusChangeEvent;
import com.wxcs.order.revivew.model.Order;
import com.wxcs.order.revivew.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Component;

@Component
public class OrderStateHandler {
    @Autowired
    private StateMachineFactory<OrderStatus, OrderStatusChangeEvent> orderStateMachineFactory;
    @Autowired
    private StateMachinePersister<OrderStatus, OrderStatusChangeEvent, String> persister;
    @Autowired
    private OrderRepository orderRepository;

    /**
     * 发送订单状态转换事件
     *
     * @param message
     * @param order
     * @return
     */
    public boolean sendEvent(Message<OrderStatusChangeEvent> message, Order order) {
        synchronized (String.valueOf(order.getId()).intern()) {
            boolean result = false;
            StateMachine<OrderStatus, OrderStatusChangeEvent> orderStateMachine = orderStateMachineFactory.getStateMachine(OrderStateMachineConfig.orderStateMachineId);
            System.out.println("id=" + order.getId() + " 状态机 orderStateMachine" + orderStateMachine);
            try {
                orderStateMachine.start();
                //尝试恢复状态机状态
                if (!message.getPayload().name().equals(OrderStatusChangeEvent.PAYED.name()))
                    persister.restore(orderStateMachine, order.getId());
                System.out.println("id=" + order.getId() + " 状态机 orderStateMachine id=" + orderStateMachine.getId());
                //添加延迟用于线程安全测试
                //Thread.sleep(1000);
                result = orderStateMachine.sendEvent(message);
                //持久化状态机状态
                persister.persist(orderStateMachine, order.getId());
                orderRepository.save(order);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                orderStateMachine.stop();
            }
            return result;
        }
    }
}
