import java.io.DataInputStream;
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
                String perfil = obtenerPerfilDeCliente(socket);
                HiloDeCliente cliente = new HiloDeCliente(socket, perfil);
                clientes.add(cliente);
                new Thread(cliente).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String obtenerPerfilDeCliente(Socket socket) {
        try {
            DataInputStream dataInput = new DataInputStream(socket.getInputStream());
            String perfil = dataInput.readUTF(); // Lee el perfil enviado por el cliente
            return perfil;
        } catch (IOException e) {
            e.printStackTrace();
            return "desconocido"; // Valor por defecto en caso de error
        }
    }
}
