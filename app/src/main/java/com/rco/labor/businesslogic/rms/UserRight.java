package com.rco.labor.businesslogic.rms;

public class UserRight implements Comparable {
    public UserRight() {
    }

    public UserRight(String Name)
    {
        this.Name = Name;
    }

    public long UserId;
    public String Name;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public long getUserId() {
        return UserId;
    }

    public void setUserId(long userId) {
        UserId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserRight) || ((UserRight)o).getName() == null) return false;
        return ((UserRight)o).getName().equals(getName());
    }

    @Override
    public int compareTo(Object another) {
        if (another == null || !(another instanceof UserRight)) return -1;
        return this.getName().compareTo(((UserRight) another).getName());
    }

    @Override
    public String toString() {
        return "UserRight{" +
                "Name='" + Name + '\'' +
                '}';
    }
}
