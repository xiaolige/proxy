package com.luna.hchat.netty;

import io.netty.channel.Channel;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2019/6/1.
 */
public class UserChannelMap {
    private static Map<String,Channel> userChannelMap;
    static {
        userChannelMap = new HashMap<String,Channel>();
    }
    public static void put(String userid,Channel channel){
        userChannelMap.put(userid,channel);
    };
    public static  void remove(String userid){
        userChannelMap.remove(userid);
    };
    public static  void  print(){
        for (String s : userChannelMap.keySet()) {
            System.out.println("userid:"+s+",channel:"+userChannelMap.get(s).id());
        }
    }
    public  static  void removeByChannelId(String channelId){
        if(!StringUtils.isNotBlank(channelId)){
            return;
        }
        for (String s : userChannelMap.keySet()) {
            Channel channel = userChannelMap.get(s);
            if(channelId.equals(channel.id().asLongText())){
                System.out.println("disconnect userid"+s+",channel:"+channel.id());
                userChannelMap.remove(s);
                break;
            }

        }
    }

    public static Channel get(String friendid) {
        return  userChannelMap.get(friendid);
    }
}
