import netty.NettyServer;

public class Server {
    public static void main(String[] args) {
        new NettyServer(8888).run();
//        new NettyServer(8889).run();
    }
}
