package com.example.nishantgahlawat.todorecycler;

import java.io.Serializable;

/**
 * Created by Nishant Gahlawat on 09-07-2017.
 */

public class ToDoItem implements Serializable{

    private long id;
    private String title;
    private String description;
    private boolean done;
    private long created;
    private long reminder;

    public ToDoItem(long id, String title, String description, boolean done, long created) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.done = done;
        this.created = created;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getReminder() {
        return reminder;
    }

    public void setReminder(long reminder) {
        this.reminder = reminder;
    }

    public boolean hasReminder() {
        return (reminder==-1)?false:true;
    }

    public void toggleDone() {
        done = !done;
    }
}
