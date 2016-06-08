package com.codepath.simpletodo.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Defines what fields does an item in a list has and is Parcelable to intent passing.
 *
 * @author Yogesh Shrivastava.
 */
public class Item implements Parcelable {
    /**
     * Variable for storing items with specific details.
     */
    private Long _id; // ID required by cupboard for storing the data.
    private String name; // Name of the item
    private String note; // Additional notes for the item
    private String dueDate; // Due date that needs to be stored
    private int priority; // Priority of the item
    private boolean done; // if item is completed or not

    public Item() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public Long getId() {
        return _id;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this._id);
        dest.writeString(this.name);
        dest.writeString(this.note);
        dest.writeString(this.dueDate);
        dest.writeInt(this.priority);
        dest.writeByte(this.done ? (byte) 1 : (byte) 0);
    }

    protected Item(Parcel in) {
        this._id = (Long) in.readValue(Long.class.getClassLoader());
        this.name = in.readString();
        this.note = in.readString();
        this.dueDate = in.readString();
        this.priority = in.readInt();
        this.done = in.readByte() != 0;
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel source) {
            return new Item(source);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
}
