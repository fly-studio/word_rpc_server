package org.fly.rpc_server.struct;

import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;

public class Request<T extends Message> {
    public int ack;
    public int version;
    public byte[] protocol = new byte[2];
    public T data;
    public ChannelHandlerContext context;
}
