import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class Server {
    private static final int CONNECTION_PORT = 23334;

    public static void main(String[] args) {
        startServer();
    }

    public static void startServer() {
        try {
            final ServerSocketChannel server = ServerSocketChannel.open();
            server.bind(new InetSocketAddress("localhost", CONNECTION_PORT));
            while (true) {
                try (SocketChannel socketChannel = server.accept()) {
                    System.out.printf("Server started at %d port\n", CONNECTION_PORT);
                    final ByteBuffer inputBuffer = ByteBuffer.allocate(2 << 10);
                    while (socketChannel.isConnected()) {
                        int bytesCount = socketChannel.read(inputBuffer);
                        if (bytesCount == -1) break;
                        final String msg = new String(inputBuffer.array(), 0, bytesCount,
                                StandardCharsets.UTF_8);
                        inputBuffer.clear();
                        try {
                            int position = Integer.parseInt(msg);
                            int result = calculate(position);
                            socketChannel.write(ByteBuffer.wrap((msg + "-ный член последовательности Фибоначчи равен: " +
                                    result).getBytes(StandardCharsets.UTF_8)));
                        } catch (IllegalArgumentException ex) {
                            socketChannel.write(ByteBuffer.wrap(("Ошибка! Вы ввели не число. Попробуйте еще раз").getBytes(StandardCharsets.UTF_8)));
                        }
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static int calculate(int position) {
        if (position < 3) {
            return 0;
        }
        if (position == 3) {
            return 1;
        }
        int result = 0;
        int prePrev = 1;
        int prev = 1;
        for (int i = 4; i < position; i++) {
            result = prePrev + prev;
            prePrev = prev;
            prev = result;
        }
        return result;
    }

}