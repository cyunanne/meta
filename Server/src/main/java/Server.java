import netty.FileServer;
import netty.MessageServer;

public class Server {
    public static void main(String[] args) {

        new Thread(new MessageServer(8888)).start();
        new Thread(new FileServer(8889)).start();

    }
}
