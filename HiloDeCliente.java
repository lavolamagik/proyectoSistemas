import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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

    public HiloDeCliente(Socket socket, Usuario usuario) {
        try {
            this.usuario = usuario;
            System.out.println("Usuario: " + usuario);
            System.out.println("Clase: " + usuario.getClass().getName());
            dataInput = new DataInputStream(socket.getInputStream());
            dataOutput = new DataOutputStream(socket.getOutputStream());
            clientes.add(this);
            cargarMensajes(); // Cargar mensajes anteriores
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
                            String mensajeParaGuardar = correoUsuario() + ": " + mensajePrivado;
                            guardarMensaje("[Privado de " + correoUsuario() + "]: " + mensajeParaGuardar);
                            cliente.dataOutput.writeUTF("[Privado de " + correoUsuario() + "]: " + mensajePrivado);
                            break;
                        }
                    }
                } else if (mensaje.startsWith("/")) {
                    String[] partes = mensaje.split(":", 2);
                    String destinatario = partes[0].substring(1); // Obtener destinatario
                    String mensajeGrupo = partes[1];

                    for (HiloDeCliente cliente : clientes) {
                        System.out.println("Cliente clase: " + cliente.usuario.getClass().getSimpleName());
                        System.out.println("Destinatario: " + destinatario);
                        if (cliente.usuario.getClass().getSimpleName().equals(destinatario)) {
                            String mensajeParaGuardar = correoUsuario() + " para grupo de " + destinatario + ": "
                                    + mensajeGrupo;
                            guardarMensaje("[ " + correoUsuario() + " para grupo de " + destinatario + "]: "
                                    + mensajeParaGuardar);
                            cliente.dataOutput.writeUTF(
                                    "[ " + correoUsuario() + " para grupo de " + destinatario + "]: " + mensajeGrupo);
                        }
                    }
                } else {
                    // Enviar mensaje general a todos
                    for (HiloDeCliente cliente : clientes) {
                        String mensajeParaGuardar = correoUsuario() + ": " + mensaje;
                        System.out.println("Guardando mensaje: " + mensajeParaGuardar);
                        guardarMensaje(mensajeParaGuardar); // Guardar en archivo
                        cliente.dataOutput.writeUTF(correoUsuario() + ": " + mensaje);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // manejo de desconexiones
            try {
                clientes.remove(this);
                enviarListaUsuarios();
                dataInput.close();
                dataOutput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Método para enviar la lista de usuarios a cada cliente
    private void enviarListaUsuarios() {
        StringBuilder listaUsuarios = new StringBuilder("#usuarios:");
        for (HiloDeCliente cliente : clientes) {
            listaUsuarios.append(cliente.correoUsuario()).append(",").append(cliente.usuario.getClass().getSimpleName())
                    .append(";"); // Añadir el usuario a la lista
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

    private void guardarMensaje(String mensaje) {
        // Lógica para guardar el mensaje al fichero
        try (FileWriter fw = new FileWriter("historial.txt", true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw)) {
            // Leer el último mensaje guardado
            String ultimoMensaje = "";
            try (BufferedReader br = new BufferedReader(new FileReader("historial.txt"))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    ultimoMensaje = linea;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Verificar si el mensaje es igual al último mensaje guardado
            if (ultimoMensaje.equals(mensaje)) {
                return;
            } else {
                out.println(mensaje);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cargarMensajes() {
        try (BufferedReader br = new BufferedReader(new FileReader("historial.txt"))) {
            String line;
            // Enviar el historial solo al cliente que se está reconectando
            while ((line = br.readLine()) != null) {
                // solo enviar mensaje si no ha sido enviado previamente
                dataOutput.writeUTF(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
