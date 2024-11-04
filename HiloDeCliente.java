import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import modelos.Usuario;
import modelos.Admin;
import modelos.Administrativo;
import modelos.Medico;

public class HiloDeCliente implements Runnable {
    private static ArrayList<HiloDeCliente> clientes = new ArrayList<>();
    private DataInputStream dataInput;
    private DataOutputStream dataOutput;
    private Usuario usuario;

    public HiloDeCliente(Socket socket,  Usuario usuario) {
        try {
            this.usuario = usuario;
            System.out.println("Usuario: " + usuario);
            System.out.println("Clase: " + usuario.getClass().getName());
            dataInput = new DataInputStream(socket.getInputStream());
            dataOutput = new DataOutputStream(socket.getOutputStream());
            clientes.add(this);

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
                        if (cliente.correoUsuario().equals(destinatario)) {
                            cliente.dataOutput.writeUTF("[Privado de " + correoUsuario() + "]: " + mensajePrivado);
                            break;
                        }
                    }
                }
                else if(mensaje.startsWith("/")) {
                    String[] partes = mensaje.split(":", 2);
                    String destinatario = partes[0].substring(1); // Obtener destinatario
                    String mensajeGrupo = partes[1];

                    for (HiloDeCliente cliente : clientes) {
                        System.out.println("Cliente clase: "+cliente.usuario.getClass().getSimpleName());
                        System.out.println("Destinatario: "+destinatario);
                        if (cliente.usuario.getClass().getSimpleName().equals(destinatario)) {
                            cliente.dataOutput.writeUTF("[ "+ correoUsuario() +" para grupo de " + destinatario +"]: " + mensajeGrupo);
                        }
                    }
                }
                else {
                    // Enviar mensaje general a todos
                    for (HiloDeCliente cliente : clientes) {
                        cliente.dataOutput.writeUTF(correoUsuario() + ": " + mensaje);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para enviar la lista de usuarios a cada cliente
    private void enviarListaUsuarios() {
        StringBuilder listaUsuarios = new StringBuilder("#usuarios:");
        for (HiloDeCliente cliente : clientes) {
            listaUsuarios.append(cliente.correoUsuario()).append(",").append(cliente.usuario.getClass().getSimpleName()).append(";"); // Añadir el usuario a la lista
        }
        // Eliminar la última coma
        if (listaUsuarios.length() > 0) {
            listaUsuarios.setLength(listaUsuarios.length() - 1);
        }
        // Enviar la lista a todos los clientes
        for (HiloDeCliente cliente : clientes) {
            try {
                cliente.dataOutput.writeUTF(listaUsuarios.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String correoUsuario() {
        return usuario.getCorreo();
    }
}
