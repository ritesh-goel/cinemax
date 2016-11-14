package com.rg.moviescentral.model;

/**
 * Created by Ritesh Goel on 14-10-2016.
 */

public class Genere {

    private int id;
    private String name;

    public Genere(){

    }

    public Genere(int id, String name) {
        this.id = id;
        this.name = name;
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
}
