package com.luna.hchat.service.impl;

import com.luna.hchat.mapper.TbFriendMapper;
import com.luna.hchat.mapper.TbFriendReqMapper;
import com.luna.hchat.mapper.TbUserMapper;
import com.luna.hchat.pojo.*;
import com.luna.hchat.pojo.vo.FriendReq;
import com.luna.hchat.pojo.vo.User;
import com.luna.hchat.service.FriendService;
import com.luna.hchat.utils.IdWorker;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2019/6/1.
 */
@Service
@Transactional
public class FriendServiceImpl  implements FriendService {
    @Autowired
    IdWorker idWorker;
    @Autowired
    private TbFriendReqMapper  friendReqMapper;
    @Autowired
    private TbFriendMapper  friendMapper;
    @Autowired
    private TbUserMapper  userMapper;

    @Override
    public void sendRequest(String fromUserid, String toUserid) {
        TbUser friend = userMapper.selectByPrimaryKey(toUserid);
        checkAllowToAddFriend(fromUserid, friend);

        TbFriendReq friendReq = new TbFriendReq();
        friendReq.setId(idWorker.nextId());
        friendReq.setFromUserid(fromUserid);
        friendReq.setToUserid(toUserid);
        friendReq.setCreatetime(new Date());
        friendReq.setStatus(0);

        friendReqMapper.insert(friendReq);

    }

    private void checkAllowToAddFriend(String userid, TbUser friend) {
        if(friend.getId().equals(userid)){
            throw new RuntimeException("不能添加自己为好友");
        }
        TbFriendExample friendExample = new TbFriendExample();
        TbFriendExample.Criteria friendCriteria1 = friendExample.createCriteria();
        friendCriteria1.andUseridEqualTo(userid);
        friendCriteria1.andFriendsIdEqualTo(friend.getId());

        List<TbFriend> friendList = friendMapper.selectByExample(friendExample);
        if(friendList!=null && friendList.size()>0){
            throw new RuntimeException(friend.getUsername() +"已经是你的好友了");
        }
        TbFriendReqExample friendReqExample = new TbFriendReqExample();
        TbFriendReqExample.Criteria friendReqCriteria1 = friendReqExample.createCriteria();
        friendReqCriteria1.andFromUseridEqualTo(userid);
        friendReqCriteria1.andToUseridEqualTo(friend.getId());
        friendReqCriteria1.andStatusEqualTo(0);

        List<TbFriendReq> friendReqList = friendReqMapper.selectByExample(friendReqExample);
        if(friendReqList!=null && friendReqList.size()>0){
            throw  new RuntimeException("已经申请过了");

        }
    }

    @Override
    public List<FriendReq> findFriendReqByUserid(String userid) {
        TbFriendReqExample example = new TbFriendReqExample();
        TbFriendReqExample.Criteria criteria = example.createCriteria();
        criteria.andToUseridEqualTo(userid);
        criteria.andStatusEqualTo(0);
        List<TbFriendReq> friendReqList= friendReqMapper.selectByExample(example);
        List<FriendReq>  friendUserList = new ArrayList<FriendReq>();
        for (TbFriendReq tbFriendReq : friendReqList) {
            TbUser tbUser = userMapper.selectByPrimaryKey(tbFriendReq.getFromUserid());
            FriendReq friendReq = new FriendReq();
            BeanUtils.copyProperties(tbUser,friendReq);
            friendReq.setId(tbFriendReq.getId());
            friendUserList.add(friendReq);
        }
        return friendUserList;
    }

    @Override
    public void acceptFriendReq(String reqid) {
        TbFriendReq friendReq = friendReqMapper.selectByPrimaryKey(reqid);
        friendReq.setStatus(1);
        friendReqMapper.updateByPrimaryKey(friendReq);

        TbFriend friend1= new TbFriend();
        friend1.setId(idWorker.nextId());
        friend1.setUserid(friendReq.getFromUserid());
        friend1.setFriendsId(friendReq.getToUserid());
        friend1.setCreatetime(new Date());

        TbFriend friend2= new TbFriend();
        friend2.setId(idWorker.nextId());
        friend2.setUserid(friendReq.getToUserid());
        friend2.setFriendsId(friendReq.getFromUserid());
        friend2.setCreatetime(new Date());

        friendMapper.insert(friend1);
        friendMapper.insert(friend2);
    }

    @Override
    public void ignoreFriendReq(String reqid) {
        TbFriendReq tbFriendReq = friendReqMapper.selectByPrimaryKey(reqid);
        tbFriendReq.setStatus(1);
        friendReqMapper.updateByPrimaryKey(tbFriendReq);
    }

    @Override
    public List<User> findFriendsByUserid(String userid) {
        TbFriendExample example = new TbFriendExample();
        final TbFriendExample.Criteria criteria = example.createCriteria();
        criteria.andUseridEqualTo(userid);

        List<TbFriend> tbFriendList = friendMapper.selectByExample(example);
        List<User> users = new ArrayList<>();
        for (TbFriend friend : tbFriendList) {
            TbUser tbUser = userMapper.selectByPrimaryKey(friend.getFriendsId());
            User user = new User();
            BeanUtils.copyProperties(tbUser,user);
            users.add(user);


        }
        return users;
    }
}
