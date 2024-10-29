import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class HiloDeCliente implements Runnable {
    private static ArrayList<HiloDeCliente> clientes = new ArrayList<>();
    private DataInputStream dataInput;
    private DataOutputStream dataOutput;
    private String nombreUsuario;

    public HiloDeCliente(Socket socket) {
        try {
            dataInput = new DataInputStream(socket.getInputStream());
            dataOutput = new DataOutputStream(socket.getOutputStream());
            clientes.add(this);

            // Obtener un nombre único para el usuario
            nombreUsuario = "Cliente" + clientes.size();
            enviarListaUsuarios();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String mensaje = dataInput.readUTF();

                if (mensaje.startsWith("@")) {
                    // Lógica para mensaje privado
                    String[] partes = mensaje.split(":", 2);
                    String destinatario = partes[0].substring(1); // Obtener destinatario
                    String mensajePrivado = partes[1];

                    for (HiloDeCliente cliente : clientes) {
                        if (cliente.nombreUsuario.equals(destinatario)) {
                            cliente.dataOutput.writeUTF("[Privado de " + nombreUsuario + "]: " + mensajePrivado);
                            break;
                        }
                    }
                } else {
                    // Enviar mensaje general a todos
                    for (HiloDeCliente cliente : clientes) {
                        cliente.dataOutput.writeUTF(nombreUsuario + ": " + mensaje);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para enviar la lista de usuarios a cada cliente
    private void enviarListaUsuarios() {
        for (HiloDeCliente cliente : clientes) {
            try {
                cliente.dataOutput.writeUTF("#usuarios:" + nombreUsuario);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
