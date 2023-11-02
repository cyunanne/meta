package netty;

import netty.initializer._FileInitializer;

public class _FileSender extends NettyClient {

    private String filename;

    public _FileSender(String host, int port, String filename) throws Exception {
        super(host, port, new _FileInitializer());
        this.filename = filename;
    }

    @Override
    public void run() {
        try {
            channel = bootstrap.connect(host, port).sync().channel();
            channel.writeAndFlush(filename).sync();

        } catch(Exception e) {
            e.printStackTrace();
            stop();
        } finally {
//            channel.close();
//            eventLoopGroup.shutdownGracefully();
        }
    }

    public void stop() {
        channel.close();
        eventLoopGroup.shutdownGracefully();
    }
}
