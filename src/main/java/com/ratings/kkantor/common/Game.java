package com.ratings.kkantor.common;

import java.util.Date;

public class Game {
    private int id;
    private String title;
    private boolean owned;
    private Date created;

    public Game() {}
    public Game(int id, String title, boolean owned, Date created) {
        this.id = id;
        this.title = title;
        this.owned = owned;
        this.created = created;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public boolean isOwned() {
        return owned;
    }
    public void setOwned(boolean owned) {
        this.owned = owned;
    }
    public Date getCreated() {
        return created;
    }
    public void setCreated(Date created) {
        this.created = created;
    }
}
