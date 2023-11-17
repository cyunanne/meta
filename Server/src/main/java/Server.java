import netty.FileServer;
import netty.MessageServer;

public class Server {
    public static void main(String[] args) {

        MessageServer ms = new MessageServer(8888);
        FileServer fs = new FileServer(8889);

        new Thread(ms).start();
        new Thread(fs).start();

    }
}
