import netty.NettyServer;
import netty.NettyServer2;

public class Server {
    public static void main(String[] args) throws Exception {
        int port = 8888;

//        new SocketServer(port).run();
//        new NettyServer(port, port+1).run();
        new NettyServer2(port).run();
    }
} 