package the.flash.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Date;

public class NettyServer {
    /**
     * 启动一个netty服务端三步：
     * 线程模型
     * IO模型
     * 数据处理逻辑
     */

    private static final int PORT = 8000;

    public static void main(String[] args) {
        //表示监听端口，accept新连接的线程组
        NioEventLoopGroup boosGroup = new NioEventLoopGroup();
        //表示处理每条连接数据处理的线程组
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        final ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                //定型
                .group(boosGroup, workerGroup)
                // 指定IO模型Nio   BIO--OioServerSocketChannel.class
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    //定义后续每条连接的数据读写，业务处理逻辑
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline().addLast(new ServerHandler());
                    }
                });

        bind(serverBootstrap, PORT);
    }

    /**
     * 监听端口是否绑定成功
     * @param serverBootstrap  引导类
     * @param port 端口
     */
    private static void bind(final ServerBootstrap serverBootstrap, final int port) {
        //异步方法
        serverBootstrap.bind(port).addListener(future -> {
            if (future.isSuccess()) {
                System.out.println(new Date() + ": 端口[" + port + "]绑定成功!");
            } else {
                System.err.println("端口[" + port + "]绑定失败!");
            }
        });
    }
}
