package com.luna.hchat.service;

import com.luna.hchat.pojo.TbUser;
import com.luna.hchat.pojo.vo.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created by Administrator on 2019/5/29.
 */
public interface UserService {
    List<TbUser> findAll();

    User login(String username, String password);

    void register(TbUser user);

    User upload(MultipartFile file, String userid);

    void updateNickname(String id, String nickname);

    User findById(String userid);

    User findByUsername(String userid, String friendUsername);
}
