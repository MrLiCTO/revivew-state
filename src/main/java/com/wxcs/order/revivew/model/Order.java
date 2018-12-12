package com.wxcs.order.revivew.model;


import com.wxcs.order.revivew.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Setter
@Getter
@Document(value = "order")
public class Order implements Serializable {
    @Id
    private String id;
    private String orderNo;
    private String desc;
    private OrderStatus status;
}
