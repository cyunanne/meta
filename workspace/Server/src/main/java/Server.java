import netty.NettyServer;

public class Server {
    public static void main(String[] args) throws Exception {
        int port = 8888;

//        new SocketServer(port).run();
        new NettyServer(port).run();
    }
} 