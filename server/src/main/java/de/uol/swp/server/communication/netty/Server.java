package de.uol.swp.server.communication.netty;

import java.net.InetSocketAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.uol.swp.common.MyObjectDecoder;
import de.uol.swp.common.MyObjectEncoder;
import de.uol.swp.server.usermanagement.UserManagement;

/**
 * This class handles opening a port clients can connect to.
 *
 * @author Marco Grawunder
 * @since 2019-11-20
 */
public class Server {

    private static final Logger LOG = LogManager.getLogger(Server.class);
    private final ChannelHandler serverHandler;
    private UserManagement userManagement;

    /**
     * Constructor
     * <p>
     * Creates a new Server Object
     *
     * @author Marco Grawunder
     * @see io.netty.channel.ChannelHandler
     * @see de.uol.swp.server.communication.ServerHandler
     * @since 2019-11-20
     */
    public Server(ChannelHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    /**
     * Start a new server on given port
     *
     * @param port port number the server shall be reachable on
     * @throws Exception server failed to start e.g. because the port is already in use
     * @author Marco Grawunder
     * @author Marco Grawunder
     * @see InetSocketAddress
     * @since 2019-11-20
     * <p>
     * Enhanced.
     * <p>
     * Just added that the connection to the database will be closed, if the server is going to shut down.
     * @since 2021-01-18
     */
    public void start(int port) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port)).childHandler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel ch) {
                    // Encoder and decoder are both needed! Send and
                    // receive serializable objects
                    ch.pipeline().addLast(new MyObjectEncoder());
                    ch.pipeline().addLast(new MyObjectDecoder(ClassResolvers.cacheDisabled(null)));
                    // must be last in the pipeline else they will not
                    // get encoded/decoded objects but ByteBuf
                    ch.pipeline().addLast(serverHandler);
                }

            });
            // Just wait for server shutdown
            ChannelFuture f = b.bind().sync();
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully().sync();
            workerGroup.shutdownGracefully().sync();
        }
    }


}
