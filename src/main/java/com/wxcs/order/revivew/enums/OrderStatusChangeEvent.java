package com.wxcs.order.revivew.enums;

/**
 * 订单状态改变事件
 */
public enum OrderStatusChangeEvent {
    // 支付，发货，确认收货
    CANCEL, PAYED, REFUND, DELIVERY, RECEIVED, RETURN, RECEIVERETURN;
}