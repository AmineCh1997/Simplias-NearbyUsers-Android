package com.example.simplias_nearby_users.entities;

import org.osmdroid.util.GeoPoint;

public class User {
    int id ;
    String name;
    GeoPoint old_position ;
    GeoPoint new_position ;

    public User(int id, String name, GeoPoint old_position, GeoPoint new_position) {
        this.id = id;
        this.name = name;
        this.old_position = old_position;
        this.new_position = new_position;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GeoPoint getOld_position() {
        return old_position;
    }

    public void setOld_position(GeoPoint old_position) {
        this.old_position = old_position;
    }

    public GeoPoint getNew_position() {
        return new_position;
    }

    public void setNew_position(GeoPoint new_position) {
        this.new_position = new_position;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", old_position=" + old_position +
                ", new_position=" + new_position +
                '}';
    }
}
