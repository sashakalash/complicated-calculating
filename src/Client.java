import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        startClient();
    }

    public static void startClient() {
        InetSocketAddress socketAddress = new InetSocketAddress("127.0.0.1",
                23334);
        try {
            final SocketChannel socketChannel = SocketChannel.open();
            socketChannel.connect(socketAddress);
            System.out.println("Введите число для расчета последовательности Фибоначчи");

            try (Scanner scanner = new Scanner(System.in)) {
                final ByteBuffer inputBuffer = ByteBuffer.allocate(2 << 10);
                String msg;
                while (true) {
                    msg = scanner.nextLine();
                    if ("end".equals(msg)) break;
                    socketChannel.write(
                            ByteBuffer.wrap(
                                    msg.getBytes(StandardCharsets.UTF_8)));
                    int bytesCount = socketChannel.read(inputBuffer);
                    while(bytesCount == -1) {
                        Thread.onSpinWait();
                    }
                    System.out.println(new String(inputBuffer.array(), 0, bytesCount,
                            StandardCharsets.UTF_8).trim());
                    inputBuffer.clear();
                }
            } finally {
                socketChannel.close();
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }
}