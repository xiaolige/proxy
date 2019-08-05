package com.luna.hchat.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * Created by Administrator on 2019/6/1.
 */
public class HeartbeatHander extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
       if(evt instanceof IdleStateEvent){
           IdleStateEvent idleStateEvent =(IdleStateEvent) evt;
           if(idleStateEvent.state() == IdleState.READER_IDLE){
               System.out.println("read idle trigger");
           }else if(idleStateEvent.state() == IdleState.WRITER_IDLE){
               System.out.println("write idle trigger");
           }else if(idleStateEvent.state() == IdleState.ALL_IDLE){
               System.out.println("--------------------------");
               System.out.println("readwrite idle trigger");
               ctx.channel().close();
           }
       }

    }
}
