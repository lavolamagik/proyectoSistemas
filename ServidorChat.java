import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServidorChat {
    private static final int PUERTO = 5000;
    private static ArrayList<HiloDeCliente> clientes = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            System.out.println("Servidor iniciado...");
            while (true) {
                Socket socket = servidor.accept();
                HiloDeCliente cliente = new HiloDeCliente(socket);
                clientes.add(cliente);
                new Thread(cliente).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
