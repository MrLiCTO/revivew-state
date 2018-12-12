package com.wxcs.order.revivew.controller;

import com.wxcs.order.revivew.model.Order;
import com.wxcs.order.revivew.po.PageInfo;
import com.wxcs.order.revivew.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping
public class OrderController {
    @Autowired
    private OrderService orderService;

    @RequestMapping(value = "list", method = RequestMethod.GET)
    public ModelAndView list(@RequestParam(defaultValue = "1") Integer pageNo, @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Order> pageInfo = orderService.list(pageNo, pageSize);
        ModelAndView modelAndView = new ModelAndView("list");
        modelAndView.addObject("list", pageInfo);
        return modelAndView;
    }

    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String create(Order order) {
        orderService.create(order);
        return "redirect:/list";
    }

    @RequestMapping(value = "delete", method = RequestMethod.GET)
    public String delete(String id) {
        orderService.delete(id);
        return "redirect:/list";
    }

    @RequestMapping(value = "pay", method = RequestMethod.GET)
    public String pay(String id) {
        orderService.pay(id);
        return "redirect:/list";
    }

    @RequestMapping(value = "deliver", method = RequestMethod.GET)
    public String deliver(String id) {
        orderService.deliver(id);
        return "redirect:/list";
    }

    @RequestMapping(value = "receive", method = RequestMethod.GET)
    public String receive(String id) {
        orderService.receive(id);
        return "redirect:/list";
    }
}
