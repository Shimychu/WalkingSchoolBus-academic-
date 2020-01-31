package com.group.peach.thewalkingschoolbus.model;

/**
 * User object model is used to grab JSON data from the server.
 */
public class ObjectIDModel {
    private Long id;
    private String href;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
