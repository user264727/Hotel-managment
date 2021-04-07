package com.example.hotelmanagement.Models;

import java.io.Serializable;

public class Room implements Serializable {

    String id;
    String roomNo;
    String floor;
    double price;
    String note;
    String type;

    public Room( String roomNo, String floor, double price, String note, String type) {
        this.roomNo = roomNo;
        this.floor = floor;
        this.price = price;
        this.note = note;
        this.type = type;
    }

    public Room() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
