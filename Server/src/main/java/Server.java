import netty.FileServer;

public class Server {
    public static void main(String[] args) {
        new Thread(new FileServer(8889)).start();
    }
}
