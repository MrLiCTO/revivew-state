package com.wxcs.order.revivew.config;


import com.wxcs.order.revivew.enums.OrderStatus;
import com.wxcs.order.revivew.enums.OrderStatusChangeEvent;
import com.wxcs.order.revivew.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.data.mongodb.MongoDbPersistingStateMachineInterceptor;
import org.springframework.statemachine.data.mongodb.MongoDbRepositoryStateMachinePersist;
import org.springframework.statemachine.data.mongodb.MongoDbStateMachineRepository;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;

import java.util.EnumSet;

/**
 * 订单状态机配置
 */
@Configuration
//@EnableStateMachine(name = "orderStateMachine")
@EnableStateMachineFactory(name = "orderStateMachineFactory")// 状态机工厂
public class OrderStateMachineConfig extends StateMachineConfigurerAdapter<OrderStatus, OrderStatusChangeEvent> {
    /**
     * 订单状态机ID
     */
    public static final String orderStateMachineId = "orderStateMachineId";

    @Autowired
    public MongoDbStateMachineRepository mongodbStateMachineRepository;


    /**
     * 配置状态
     *
     * @param states
     * @throws Exception
     */
    @Override
    public void configure(StateMachineStateConfigurer<OrderStatus, OrderStatusChangeEvent> states) throws Exception {
        states
                .withStates()
                .initial(OrderStatus.WAIT_PAYMENT)
                .states(EnumSet.allOf(OrderStatus.class));
    }

    /**
     * 配置状态转换事件关系
     *
     * @param transitions
     * @throws Exception
     */
    @Override
    public void configure(StateMachineTransitionConfigurer<OrderStatus, OrderStatusChangeEvent> transitions) throws Exception {
        transitions
                .withExternal().source(OrderStatus.WAIT_PAYMENT).target(OrderStatus.WAIT_DELIVER).event(OrderStatusChangeEvent.PAYED)//支付
                .and()
                .withExternal().source(OrderStatus.WAIT_PAYMENT).target(OrderStatus.CLOSED).event(OrderStatusChangeEvent.CANCEL)//1 取消
                .and()
                .withExternal().source(OrderStatus.WAIT_DELIVER).target(OrderStatus.WAIT_RECEIVE).event(OrderStatusChangeEvent.DELIVERY)//发货
                .and()
                .withExternal().source(OrderStatus.WAIT_RECEIVE).target(OrderStatus.FINISH).event(OrderStatusChangeEvent.RECEIVED)//收货
                .and()
                .withExternal().source(OrderStatus.WAIT_RECEIVE).target(OrderStatus.RETURN).event(OrderStatusChangeEvent.REFUND)//2退款
                .and()
                .withExternal().source(OrderStatus.WAIT_DELIVER).target(OrderStatus.RETURN).event(OrderStatusChangeEvent.REFUND)//2退款
                .and()
                .withExternal().source(OrderStatus.FINISH).target(OrderStatus.WAIT_RETURN).event(OrderStatusChangeEvent.RETURN)//3退货
                .and()
                .withExternal().source(OrderStatus.WAIT_RETURN).target(OrderStatus.RETURN).event(OrderStatusChangeEvent.RECEIVERETURN);//4退货完成
    }

    /**
     * 配置相关监听器或者其他信息
     * @param config
     * @throws Exception
     */
    @Override
    public void configure(StateMachineConfigurationConfigurer<OrderStatus, OrderStatusChangeEvent> config) throws Exception {
        config.withConfiguration().machineId(orderStateMachineId);
    }

    //持久化---mongo---start
    @Bean
    public MongoDbRepositoryStateMachinePersist<OrderStatus, OrderStatusChangeEvent> mongoDbRepositoryStateMachinePersist() {
        return new MongoDbRepositoryStateMachinePersist(mongodbStateMachineRepository);
    }

    @Bean
    public MongoDbPersistingStateMachineInterceptor<OrderStatus, OrderStatusChangeEvent, Order> interceptor() {
        MongoDbPersistingStateMachineInterceptor interceptor = new MongoDbPersistingStateMachineInterceptor(mongoDbRepositoryStateMachinePersist());
        return interceptor;
    }

    @Bean
    public StateMachinePersister<OrderStatus, OrderStatusChangeEvent, String> stateMachinePersister() {
        return new DefaultStateMachinePersister(mongoDbRepositoryStateMachinePersist());
    }
    //持久化---mongo---end
}