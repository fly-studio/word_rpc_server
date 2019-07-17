package org.fly.rpc_server.protocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.fly.rpc_server.executor.Executor;
import org.fly.rpc_server.server.Server;
import org.fly.rpc_server.struct.Request;
import org.fly.rpc_server.struct.Response;
import org.fly.rpc_server.struct.Rpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcChannel extends SimpleChannelInboundHandler {
    private final static Logger logger = LoggerFactory.getLogger(RpcChannel.class);
    private static Executor executor = new Executor();

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg == null)
            return;

        executor.exec((Request) msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        logger.info("Client connect from: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

        logger.info("Client disconnect from: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("[Exception] {} of {}", cause.getMessage(), ctx.channel().remoteAddress());
        logger.error(cause.getMessage(), cause);
        ctx.close();
    }
}
