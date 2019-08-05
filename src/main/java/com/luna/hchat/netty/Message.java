package com.luna.hchat.netty;

import com.luna.hchat.pojo.TbChatRecord;

/**
 * Created by Administrator on 2019/6/1.
 */
public class Message {

    private String type;
    private TbChatRecord chatRecord;
    private Object ext;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public TbChatRecord getChatRecord() {
        return chatRecord;
    }

    public void setChatRecord(TbChatRecord chatRecord) {
        this.chatRecord = chatRecord;
    }

    public Object getExt() {
        return ext;
    }

    public void setExt(Object ext) {
        this.ext = ext;
    }
}
