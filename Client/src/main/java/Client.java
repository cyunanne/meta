import netty.NettyClient;

import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        new NettyClient("localhost", 8888).run();
    }
}
