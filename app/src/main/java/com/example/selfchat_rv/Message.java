package com.example.selfchat_rv;

public class Message {
    private String Id;
    private String TimeStamp;
    private String Text;

    public Message(String Id, String time, String text)
    {
        Text = text;
        TimeStamp = time;
        this.Id = Id;
    }
    @Override
    public String toString()
    {
        return this.TimeStamp + ":" + Text;
    }

    public String getId() {
        return Id;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }
    public String getText() {
        return Text;
    }

}

