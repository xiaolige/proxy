package com.luna.hchat.service;

import com.luna.hchat.pojo.vo.FriendReq;
import com.luna.hchat.pojo.vo.User;

import java.util.List;

/**
 * Created by Administrator on 2019/6/1.
 */
public interface FriendService {
    void sendRequest(String fromUserid, String toUserid);

    List<FriendReq> findFriendReqByUserid(String userid);

    void acceptFriendReq(String reqid);

    void ignoreFriendReq(String reqid);

    List<User> findFriendsByUserid(String userid);
}
