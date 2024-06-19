package com.example.ech.notes;

public class Notes {
    private String message;
    private int mid;



    public Notes(String message,int mid)
    {
        this.message=message;
        this.mid=mid;
    }
    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}