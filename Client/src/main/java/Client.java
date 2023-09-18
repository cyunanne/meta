public class Client {

    private static final String host = "localhost";
    private static final int port = 8888;

    public static void main(String[] args) throws Exception {
        new SocketWithNetty(host, port).run();
    }
}