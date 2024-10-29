import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClienteChat {
    private ClienteChatGUI gui;
    private Socket socket;
    private DataInputStream dataInput;
    private DataOutputStream dataOutput;

    public ClienteChat() {
        try {
            socket = new Socket("localhost", 5000);
            gui = new ClienteChatGUI(socket);
            dataInput = new DataInputStream(socket.getInputStream());

            // Hilo para recibir mensajes
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            String mensaje = dataInput.readUTF();

                            if (mensaje.startsWith("#usuarios:")) {
                                String usuario = mensaje.split(":")[1];
                                gui.agregarUsuario(usuario);
                            } else {
                                gui.mostrarMensaje(mensaje);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ClienteChat();
    }
}
