package com.luna.hchat.controller;

import com.luna.hchat.pojo.TbChatRecord;
import com.luna.hchat.service.ChatRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/6/1.
 */
@RestController
@RequestMapping("/chatrecord")
public class ChatRecordController {
    @Autowired
    private ChatRecordService   chatRecordService;
    @RequestMapping("/findByUserIdAndFriendId")
    public List<TbChatRecord> findByUserIdAndFriendId(String userid,String friendid){
        try {
            return chatRecordService.findByUserIdAndFriendId(userid,friendid);
        } catch (Exception e) {
            e.printStackTrace();
            return  new ArrayList<TbChatRecord>();
        }
    }
    @RequestMapping("/findUnreadByUserid")
    public List<TbChatRecord>  findUnreadByUserid(String userid){
        try {
            return  chatRecordService.findUnreadByUserid(userid);
        } catch (Exception e) {
            e.printStackTrace();
            return  new ArrayList<TbChatRecord>();
        }
    }
}
