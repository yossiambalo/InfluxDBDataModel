package com.odysii.influx.payload;

public class PosBasket {
    public String getCode() {
        return code;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getDescription() {
        return description;
    }

    private String code,quantity,description;

    public PosBasket(String code, String quantity, String description) {
        this.code = code;
        this.quantity = quantity;
        this.description = description;
    }
}
