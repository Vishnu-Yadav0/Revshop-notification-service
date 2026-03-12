package com.revshop.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdersDTO {
    private String orderNumber;
    private LocalDateTime orderDate;
    private String paymentMethod;
    private BigDecimal totalAmount;
    private String status;
    private ShipperDTO shipper;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShipperDTO {
        private String name;
        private String vehicleNumber;
        private String phone;
    }
}
