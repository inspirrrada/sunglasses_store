package ua.com.alevel.facade.order.impl;

import org.springframework.stereotype.Service;
import ua.com.alevel.dto.order.*;
import ua.com.alevel.dto.user.PersonalOrdersDto;
import ua.com.alevel.facade.order.OrderFacade;
import ua.com.alevel.persistence.entity.cart.Cart;
import ua.com.alevel.persistence.entity.order.Order;
import ua.com.alevel.persistence.entity.order.OrderItem;
import ua.com.alevel.persistence.entity.user.Personal;
import ua.com.alevel.persistence.entity.user.User;
import ua.com.alevel.persistence.type.order.OrderStatusType;
import ua.com.alevel.service.cart.CartService;
import ua.com.alevel.service.order.OrderService;
import ua.com.alevel.service.user.UserService;

import java.util.*;

@Service
public class OrderFacadeImpl implements OrderFacade {

    private final OrderService orderService;
    private final UserService userService;
    private final CartService cartService;

    public OrderFacadeImpl(OrderService orderService, UserService userService, CartService cartService) {
        this.orderService = orderService;
        this.userService = userService;
        this.cartService = cartService;
    }

    @Override
    public boolean saveOrderDetails(OrderDetailsDto orderDetailsDto) {
        return true;
    }

    @Override
    public Set<PersonalOrdersDto> findAllOrdersByUser(User user) {
        Set<PersonalOrdersDto> personalOrders = new HashSet<>();
        orderService.findAllByUser(user)
                .forEach(order -> {
                    personalOrders.add(new PersonalOrdersDto(order));
                });
        return personalOrders;
    }

    @Override
    public OrderDetailsDto showOrderDetails(Personal personal) {
        return new OrderDetailsDto(personal);
    }

    @Override
    public OrderDetailsDto showOrderDetails(Order order) {
        return new OrderDetailsDto(order);
    }

    @Override
    public void createNewOrder(OrderDetailsDto orderDetailsDto, Personal personal) {
        Order order = convertOrderDetailsDtoToOrder(orderDetailsDto);
        order.setUser(personal);
        Cart cart = cartService.findById(personal.getId());
        orderService.createNewOrder(order, cart);
    }

    @Override
    public List<OrderStatusDto> getOrdersInfoForAdmin() {
        List<Order> orders = orderService.findAll();
        List<OrderStatusDto> orderStatusDtoList = new ArrayList<>();
        orders.forEach(v -> {
            OrderStatusDto orderStatusDto = convertOrderToOrderStatusDto((Order)v);
            orderStatusDtoList.add(orderStatusDto);
        });
        return orderStatusDtoList;
    }

    @Override
    public User findUserByOrderId(Order order) {
        Long orderId = order.getId();
        Long userId = orderService.findUserIdByOrderId(orderId);
        User user = userService.findById(userId);
        return user;
    }

    @Override
    public Order findById(Long id) {
        return orderService.findById(id);
    }

    @Override
    public OrderSummaryDto findAllByOrder(Long orderId) {
        Collection<OrderItem> orderItems = orderService.findAllByOrder(orderId);
        return new OrderSummaryDto(orderItems
                .stream()
                .map(SunglassesOrderDto::new)
                .toList());
    }

    @Override
    public List<OrderStatusDto> findAllOrderStatusDto() {
        List<OrderStatusDto> orderStatusDtoList = new ArrayList<>();
        orderService.findAll().forEach(v-> {
            Long userId = orderService.findUserIdByOrderId(v.getId());
            User user = userService.findById(userId);
            OrderStatusDto orderStatusDto = new OrderStatusDto(v, user);
            orderStatusDtoList.add(orderStatusDto);
        });
        return orderStatusDtoList;
    }

    @Override
    public void updateOrdersStatuses(StatusFormDto statusFormDto) {
        Collection<OrderStatusDto> orderStatusDtoList = statusFormDto.getOrderStatusDtoList();
        orderService.updateOrdersStatuses(orderStatusDtoList);
    }

    private Order convertOrderDetailsDtoToOrder(OrderDetailsDto orderDetailsDto) {
        Order order = new Order();
        order.setContactFirstName(orderDetailsDto.getContactFirstName());
        order.setContactLastName(orderDetailsDto.getContactLastName());
        order.setContactPhoneNumber(orderDetailsDto.getContactPhoneNumber());
        order.setReservePhoneNumber(orderDetailsDto.getReservePhoneNumber());
        order.setDeliveryZip(orderDetailsDto.getDeliveryZip());
        order.setDeliveryRegion(orderDetailsDto.getDeliveryRegion());
        order.setDeliveryCity(orderDetailsDto.getDeliveryCity());
        order.setDeliveryStreet(orderDetailsDto.getDeliveryStreet());
        order.setDeliveryBuilding(orderDetailsDto.getDeliveryBuilding());
        order.setDeliveryApartment(orderDetailsDto.getDeliveryApartment());
        order.setOrderStatus(OrderStatusType.CREATED);
        order.setOrderNotes(orderDetailsDto.getNotes());
        order.setPaymentMethod(orderDetailsDto.getPaymentMethod());
        return order;
    }

    private OrderStatusDto convertOrderToOrderStatusDto(Order order) {
        OrderStatusDto orderStatusDto = new OrderStatusDto();
        orderStatusDto.setId(order.getId());
        orderStatusDto.setCreated(order.getCreated());
        orderStatusDto.setNumber(order.getNumber());
        orderStatusDto.setStatus(order.getOrderStatus());
        orderStatusDto.setTotalAmount(order.getTotalAmount());
        User user = findUserByOrderId(order);
        orderStatusDto.setUserEmail(user.getEmail());
        return orderStatusDto;
    }
}
