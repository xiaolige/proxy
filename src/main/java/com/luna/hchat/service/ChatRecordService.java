package com.luna.hchat.service;

import com.luna.hchat.pojo.TbChatRecord;

import java.util.List;

/**
 * Created by Administrator on 2019/6/1.
 */
public interface ChatRecordService {
    void insert(TbChatRecord chatRecord);

    List<TbChatRecord> findByUserIdAndFriendId(String userid, String friendid);

    List<TbChatRecord> findUnreadByUserid(String userid);

    void updateStatusHasRead(String id);
}
