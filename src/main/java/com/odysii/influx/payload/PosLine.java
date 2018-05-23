package com.odysii.influx.payload;

public class PosLine {
    public String getQuantity() {
        return quantity;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getCodeType() {
        return codeType;
    }

    public String getCode() {
        return code;
    }

    public String getSeq() {
        return seq;
    }

    public String getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    private String quantity,description,price,codeType,code,seq,time,type;

    public PosLine(String quantity, String description, String price, String codeType, String code, String seq, String time, String type) {
        this.quantity = quantity;
        this.description = description;
        this.price = price;
        this.codeType = codeType;
        this.code = code;
        this.seq = seq;
        this.time = time;
        this.type = type;
    }
}
