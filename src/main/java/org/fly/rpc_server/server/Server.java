package org.fly.rpc_server.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.fly.rpc_server.executor.Executor;
import org.fly.rpc_server.protocol.TagV2Encoder;
import org.fly.rpc_server.protocol.RpcChannel;
import org.fly.rpc_server.protocol.TagV2Decoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

public class Server {

    private final static Logger logger = LoggerFactory.getLogger(Server.class);

    public final static Executor executor = new Executor();

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap serverBootstrap;
    private ChannelFuture channelFuture;
    private String host;
    private int port;
    private boolean epoll;

    public Server(int port) {
        this("0.0.0.0", port, false);
    }

    public Server(String host, int port) {
        this(host, port, false);
    }

    public Server(String host, int port, boolean epoll)
    {
        this.host = host;
        this.port = port;
        this.epoll = epoll;
        init();
    }

    protected void init()
    {
        if (epoll)
        {
            bossGroup = new EpollEventLoopGroup();
            workerGroup = new EpollEventLoopGroup();
        } else {
            //处理Accept连接事件的线程，这里线程数设置为1即可，netty处理链接事件默认为单线程，过度设置反而浪费cpu资源
            bossGroup = new NioEventLoopGroup();//接收新连接线程，调度分配连接
            //处理hadnler的工作线程，其实也就是处理IO读写 。线程数据默认为 CPU 核心数乘以2
            workerGroup = new NioEventLoopGroup();//工作执行线程
        }

        serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(bossGroup, workerGroup)
                .option(ChannelOption.SO_BACKLOG, 128) //标识当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度
                .option(EpollChannelOption.SO_REUSEPORT, true)
                .option(ChannelOption.TCP_NODELAY, true) // Tcp no-delay
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childOption(ChannelOption.SO_KEEPALIVE, true) // 是否启用心跳保活机机制
                .childHandler(new RpcHandler())
                ;

        if (epoll)
            serverBootstrap.channel(EpollServerSocketChannel.class);
        else
            serverBootstrap.channel(NioServerSocketChannel.class);

    }

    public void start()
    {
        try {
            executor.run();

            // 绑定端口，开始接收进来的连接
            channelFuture = serverBootstrap.bind(host, port).sync();
            if(channelFuture.isSuccess()){
                logger.info("Netty server started.");
            }
            // 监听服务器关闭
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e)
        {
            logger.error(e.getMessage(), e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    @PreDestroy
    public void stop()
    {
        executor.shutdown();

        channelFuture.channel().close().addListener(ChannelFutureListener.CLOSE);
        channelFuture.awaitUninterruptibly();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        channelFuture = null;
    }

    private class RpcHandler extends ChannelInitializer<NioSocketChannel> {
        @Override
        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
            nioSocketChannel.pipeline()
                    .addLast(new IdleStateHandler(60, 60, 60, TimeUnit.SECONDS))
                    .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 6, 4, 0, 0))
                    .addLast("decoder", new TagV2Decoder())
                    .addLast("encoder", new TagV2Encoder())
                    .addLast(new RpcChannel())
            ;
        }
    }
}
