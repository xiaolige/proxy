package com.luna.hchat.service.impl;

import com.luna.hchat.mapper.TbChatRecordMapper;
import com.luna.hchat.pojo.TbChatRecord;
import com.luna.hchat.pojo.TbChatRecordExample;
import com.luna.hchat.service.ChatRecordService;
import com.luna.hchat.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2019/6/1.
 */
@Service
@Transactional
public class ChatRecordServiceImpl implements ChatRecordService{
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private TbChatRecordMapper  chatRecordMapper;
    @Override
    public void insert(TbChatRecord chatRecord) {
        chatRecord.setId(idWorker.nextId());
        chatRecord.setHasRead(0);
        chatRecord.setCreatetime(new Date());
        chatRecord.setHasDelete(0);

        chatRecordMapper.insert(chatRecord);
    }

    @Override
    public List<TbChatRecord> findByUserIdAndFriendId(String userid, String friendid) {
        TbChatRecordExample example = new TbChatRecordExample();
        TbChatRecordExample.Criteria criteria1 = example.createCriteria();
        TbChatRecordExample.Criteria criteria2 = example.createCriteria();
        criteria1.andUseridEqualTo(userid);
        criteria1.andFriendidEqualTo(friendid);
        criteria1.andHasDeleteEqualTo(0);

        criteria2.andUseridEqualTo(friendid);
        criteria2.andFriendidEqualTo(userid);
        criteria2.andHasDeleteEqualTo(0);

        example.or(criteria1);
        example.or(criteria2);
        TbChatRecordExample exampleQuerySendToMe = new TbChatRecordExample();
        TbChatRecordExample.Criteria criteriaQuerySendToMe = exampleQuerySendToMe.createCriteria();
        criteriaQuerySendToMe.andFriendidEqualTo(userid);
        criteriaQuerySendToMe.andHasReadEqualTo(0);


        List<TbChatRecord> tbChatRecords = chatRecordMapper.selectByExample(exampleQuerySendToMe);
        for (TbChatRecord tbChatRecord : tbChatRecords) {
            tbChatRecord.setHasRead(1);
            chatRecordMapper.updateByPrimaryKey(tbChatRecord);
        }
        return chatRecordMapper.selectByExample(example);
    }

    @Override
    public List<TbChatRecord> findUnreadByUserid(String userid) {
        TbChatRecordExample example = new TbChatRecordExample();
        TbChatRecordExample.Criteria criteria = example.createCriteria();
        criteria.andFriendidEqualTo(userid);
        criteria.andHasReadEqualTo(0);

        return  chatRecordMapper.selectByExample(example);
    }

    @Override
    public void updateStatusHasRead(String id) {
        TbChatRecord chatRecord = chatRecordMapper.selectByPrimaryKey(id);
         chatRecord.setHasRead(1);
        chatRecordMapper.updateByPrimaryKey(chatRecord);

    }
}
