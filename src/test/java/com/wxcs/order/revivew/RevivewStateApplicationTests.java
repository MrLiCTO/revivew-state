package com.wxcs.order.revivew;

import com.wxcs.order.revivew.enums.OrderStatus;
import com.wxcs.order.revivew.enums.OrderStatusChangeEvent;
import com.wxcs.order.revivew.repository.OrderRepository;
import com.wxcs.order.revivew.handler.OrderStateHandler;
import com.wxcs.order.revivew.model.Order;
import com.wxcs.order.revivew.utils.UuidUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RevivewStateApplicationTests {
    @Autowired
    OrderStateHandler orderStateHandler;
    @Autowired
    private OrderRepository orderRepository;
    @Test
    public void contextLoads() throws Exception {
        //Order order = orderRepository.findById("tttttttttttttt222261111111").get();
        Order order = new Order();
        order.setOrderNo(System.currentTimeMillis()/1000+"");
        order.setDesc("测试");
        order.setStatus(OrderStatus.WAIT_PAYMENT);
        order.setId(UuidUtil.get32UUID());
        Message<OrderStatusChangeEvent> message = MessageBuilder.withPayload(OrderStatusChangeEvent.PAYED).setHeader("order", order).build();
        orderStateHandler.sendEvent(message,order);

        message = MessageBuilder.withPayload(OrderStatusChangeEvent.DELIVERY).setHeader("order", order).build();
        orderStateHandler.sendEvent(message,order);

        message = MessageBuilder.withPayload(OrderStatusChangeEvent.RECEIVED).setHeader("order", order).build();
        orderStateHandler.sendEvent(message,order);

    }

}
