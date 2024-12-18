import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import modelos.Admin;
import modelos.Administrativo;
import modelos.Medico;
import modelos.Usuario;

public class ClienteChat {
    private ClienteGUI gui;
    private Socket socket;
    private DataInputStream dataInput;
    private DataOutputStream dataOutput;
    private Usuario usuario;

    public ClienteChat() {
        try {
            InicioSesionGui inicioSesion = new InicioSesionGui();
   
            try {
                
                socket = new Socket("34.57.136.213", 5000);
            } catch (IOException e) {
                System.out.println("Intentando reconectar...");
                Thread.sleep(1000); 
            }
            while (socket == null || !socket.isConnected()) {
                try {
                    socket = new Socket("34.57.136.213", 5000);
                    if (socket.isConnected()) {
                        System.out.println("Conectado al servidor");
                    }
                } catch (IOException e) {
                    System.out.println("Intentando reconectar...");
                    Thread.sleep(1000); 
                }
            }

            inicioSesion.setSocket(socket);

            // this.perfil = perfil;

            
            // recibir perfil de InicioSesionGui
            while (inicioSesion.getUsuario() == null) {
                Thread.sleep(100); // Esperar brevemente
            }
            dataInput = new DataInputStream(socket.getInputStream());
            dataOutput = new DataOutputStream(socket.getOutputStream());
            usuario = inicioSesion.getUsuario();
            System.out.println(usuario);
            System.out.println(usuario.getClass());
            System.out.println(Medico.class);
            if (this.usuario.getClass() == Medico.class) {
                gui = new ClienteMedicoGUI(socket, usuario);
            } else if (this.usuario.getClass() == Administrativo.class) {
                gui = new ClienteAdministrativoGUI(socket, usuario);
            } else if (this.usuario.getClass() == Admin.class) {
                gui = new ClienteAdminGUI(socket, usuario);
            }
            gui.cargarMensajes(); // Cargar mensajes anteriores

            //dataOutput.writeUTF(usuario.toString());

            // Hilo para recibir mensajes
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            String mensaje = dataInput.readUTF();

                            if (mensaje.startsWith("#usuarios:")) {
                                gui.limpiarListaUsuarios(); // Limpia la lista antes de agregar
                                System.out.println(mensaje);
                                String[] usuarios = mensaje.split(":")[1].split(";");
                                for (String usuarioAgregar : usuarios) {
                                    String[] partes = usuarioAgregar.split(","); // Dividir nombre y perfil

                                    if (partes[0].equals(usuario.getCorreo())) {
                                        continue;
                                    }

                                    if (partes[1].equals("Administrativo")) {
                                        gui.agregarAdministrativo(partes[0]);
                                    } else if (partes[1].equals("Medico")) {
                                        gui.agregarMedico(partes[0]);
                                    }

                                }
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
