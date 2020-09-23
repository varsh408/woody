package com.example.saru.Model;

public class AdminOrders
 {
     private String name, phone, address,date,city,time, state,totalAmunt;

     public AdminOrders() {
     }

     public AdminOrders(String name, String phone, String address, String date, String city, String time, String state, String totalAmunt) {
         this.name = name;
         this.phone = phone;
         this.address = address;
         this.date = date;
         this.city = city;
         this.time = time;
         this.state = state;
         this.totalAmunt = totalAmunt;
     }

     public String getName() {
         return name;
     }

     public void setName(String name) {
         this.name = name;
     }

     public String getPhone() {
         return phone;
     }

     public void setPhone(String phone) {
         this.phone = phone;
     }

     public String getAddress() {
         return address;
     }

     public void setAddress(String address) {
         this.address = address;
     }

     public String getDate() {
         return date;
     }

     public void setDate(String date) {
         this.date = date;
     }

     public String getCity() {
         return city;
     }

     public void setCity(String city) {
         this.city = city;
     }

     public String getTime() {
         return time;
     }

     public void setTime(String time) {
         this.time = time;
     }

     public String getState() {
         return state;
     }

     public void setState(String state) {
         this.state = state;
     }

     public String getTotalAmunt() {
         return totalAmunt;
     }

     public void setTotalAmunt(String totalAmunt) {
         this.totalAmunt = totalAmunt;
     }
 }
