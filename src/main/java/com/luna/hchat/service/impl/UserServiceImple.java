package com.luna.hchat.service.impl;

import com.luna.hchat.mapper.TbFriendMapper;
import com.luna.hchat.mapper.TbFriendReqMapper;
import com.luna.hchat.mapper.TbUserMapper;
import com.luna.hchat.pojo.*;
import com.luna.hchat.pojo.vo.User;
import com.luna.hchat.service.UserService;
import com.luna.hchat.utils.FastDFSClient;
import com.luna.hchat.utils.IdWorker;
import com.luna.hchat.utils.QRCodeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2019/5/29.
 */
@Service
@Transactional
public class UserServiceImple implements UserService {
    @Autowired
    private TbUserMapper userMapper;

    @Autowired
    private QRCodeUtils  qrCodeUtils;
    @Autowired
    IdWorker idWork;
    @Autowired
    FastDFSClient fastDFSClient;

    @Autowired
    private TbFriendMapper tbFriendMapper;

    @Autowired
    private TbFriendReqMapper  tbFriendReqMapper;

    @Autowired
    private Environment env;
    @Override
    public List<TbUser> findAll() {
        return userMapper.selectByExample(null);
    }

    @Override
    public User login(String username, String password) {
        if(StringUtils.isNotBlank(username)&& StringUtils.isNotBlank(password)) {
            TbUserExample example = new TbUserExample();
            TbUserExample.Criteria criteria = example.createCriteria();
            criteria.andUsernameEqualTo(username);
            List<TbUser> tbUsers = userMapper.selectByExample(example);
            if(tbUsers!=null&&tbUsers.size()==1){

                String encodingPassword = DigestUtils.md5DigestAsHex(password.getBytes());
                if(encodingPassword.equals(tbUsers.get(0).getPassword())){
                     User user =new User();
                    BeanUtils.copyProperties(tbUsers.get(0),user);
                    return  user;
                }

            }
        }
        return null;
    }

    @Override
    public void register(TbUser user) {
        try {
            TbUserExample example = new TbUserExample();
            TbUserExample.Criteria criteria = example.createCriteria();
            criteria.andUsernameEqualTo(user.getUsername());
            List<TbUser> userList = userMapper.selectByExample(example);
            if(userList!=null && userList.size()>0){
                throw  new RuntimeException("用户已经存在");
            }
            user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
            user.setPicSmall("");
            user.setPicNormal("");
            user.setNickname(user.getUsername());

            String qrcodeStr="hichat://"+user.getUsername();
            String tempDir = env.getProperty("hcat.tmpdir");
            String qrCodeFilePath =  tempDir+user.getUsername()+".png";
            qrCodeUtils.createQRCode(qrCodeFilePath,qrcodeStr);
            String url = env.getProperty("fdfs.httpurl")+fastDFSClient.uploadFile(new File(qrCodeFilePath));
            user.setQrcode(url);
            user.setClientId(user.getClientId());
            user.setCreatetime(new Date());
            user.setId(idWork.nextId());
            userMapper.insert(user);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("注册失败");
        }
    }

    @Override
    public User upload(MultipartFile file, String userid) {
        try {
            String url = fastDFSClient.uploadFace(file);
            String[] fileNameList = url.split("\\.");
            String fileName = fileNameList[0];
            String ext = fileNameList[1];
            String picSmallUrl = fileName+"_150x150."+ext;

            String prefix = env.getProperty("fdfs.httpurl");
            TbUser tbUser = userMapper.selectByPrimaryKey(userid);
            tbUser.setPicNormal(prefix +url);
            tbUser.setPicSmall(prefix+ picSmallUrl);

            userMapper.updateByPrimaryKey(tbUser);
            User user = new User();
            BeanUtils.copyProperties(tbUser,user);
            return user;
        } catch (IOException e) {
            e.printStackTrace();
            return  null;
        }
    }

    @Override
    public void updateNickname(String id, String nickname) {
        if(StringUtils.isNoneBlank(nickname)){

            TbUser user =userMapper.selectByPrimaryKey(id);
            user.setNickname(nickname);
            userMapper.updateByPrimaryKey(user);
        }else{
            throw new RuntimeException("昵称不能为空");
        }
    }

    @Override
    public User findById(String userid) {
        TbUser tbUser = userMapper.selectByPrimaryKey(userid);
        User user = new User();
        BeanUtils.copyProperties(tbUser,user);
        return  user;
    }

    @Override
    public User findByUsername(String userid, String friendUsername) {
        TbUserExample example = new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(friendUsername);

        List<TbUser> userList = userMapper.selectByExample(example);
        TbUser friend = userList.get(0);

       // checkAllowToAddFriend(userid, friend);
       User friendUser =new User();
        BeanUtils.copyProperties(friend,friendUser);
        return friendUser;
    }



}
