package the.flash;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

public class NettyServer {

    private static final int BEGIN_PORT = 8000;

    public static void main(String[] args) {
        //表示监听端口，负责accept连接的线程组
        NioEventLoopGroup boosGroup = new NioEventLoopGroup();
        //表示处理每一条连接数据读写的线程组，
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        final ServerBootstrap serverBootstrap = new ServerBootstrap();
        final AttributeKey<Object> clientKey = AttributeKey.newInstance("clientKey");
        serverBootstrap
                //定型
                .group(boosGroup, workerGroup)
                //IO模型
                .channel(NioServerSocketChannel.class)
                //给服务器的channel指定一些自定义属性 可以通过channel.attr()取出
                .attr(AttributeKey.newInstance("serverName"), "nettyServer")
                //为每一条连接指定自定义属性 可以通过channel.attr()取出
                .childAttr(clientKey, "clientValue")
                //系统用于临时存放已完成三次握手的请求的队列的最大长度，如果连接建立频繁，可以适当调大这个值
                .option(ChannelOption.SO_BACKLOG, 1024)
                //childOption可以给每条连接设置一些TCP底层相关的属性
                //底层心跳机制，true为开启
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                //是否开启Nagle算法  true表示关闭（时效性较弱，有延时），false表示开启（时效性较强）
                .childOption(ChannelOption.TCP_NODELAY, true)
                //.handler()指定在服务端启动过程中的一些逻辑
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    //定义数据处理逻辑
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        System.out.println(ch.attr(clientKey).get());
                    }
                });


        bind(serverBootstrap, BEGIN_PORT);
    }

    private static void bind(final ServerBootstrap serverBootstrap, final int port) {
        serverBootstrap.bind(port).addListener(future -> {
            if (future.isSuccess()) {
                System.out.println("端口[" + port + "]绑定成功!");
            } else {
                System.err.println("端口[" + port + "]绑定失败!");
                //若绑定失败，则端口自增再进行递归
                bind(serverBootstrap, port + 1);
            }
        });
    }
}
