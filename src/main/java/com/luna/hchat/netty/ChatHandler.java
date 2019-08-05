package com.luna.hchat.netty;


import com.alibaba.fastjson.JSON;
import com.luna.hchat.pojo.TbChatRecord;
import com.luna.hchat.service.ChatRecordService;
import com.luna.hchat.utils.SpringUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;

/**
 * 处理消息的handler
 * TextWebSocketFrame: 在netty中，是用于为websocket专门处理文本的对象，frame是消息的载体
 */
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:MM");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        String text =msg.text();
        System.out.println("recice message:"+text);
        Message message = JSON.parseObject(text, Message.class);
        ChatRecordService chatRecordService = SpringUtil.getBean(ChatRecordService.class);
        switch (message.getType()){
            case "0":
                String userid=message.getChatRecord().getUserid();
                UserChannelMap.put(userid,ctx.channel());
                System.out.println("userid:"+userid+",channel:"+ctx.channel().id());
                UserChannelMap.print();
                break;
            case "1":
                System.out.println("recieve message");
                TbChatRecord chatRecord=message.getChatRecord();

                chatRecordService.insert(chatRecord);

                Channel channel =UserChannelMap.get(chatRecord.getFriendid());
                if(null!=channel){
                    channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(message)));
                }else{
                    System.out.println("user:"+chatRecord.getFriendid()+"is offline");
                }
                break;
            case "2":
                chatRecordService.updateStatusHasRead(message.getChatRecord().getId());
                break;
            case "3":
                System.out.println("access beatheart message :"+message);
                break;


        }

    }

    /**
     * 当客户端连接服务端之后（打开连接）
     * 获取客户端的channel，并且放入到ChannelGroup中去进行管理
     *
     * @param ctx
     * @throws Exception
     */

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // 将channel添加到客户端
        clients.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // 当触发handlerRemoved，ChannelGroup会自动移除对应客户端的channel
        System.out.println("关闭通道");
        UserChannelMap.removeByChannelId(ctx.channel().id().asLongText());
       UserChannelMap.print();
       // clients.remove(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        UserChannelMap.removeByChannelId(ctx.channel().id().asLongText());
        ctx.channel().close();
    }
}
