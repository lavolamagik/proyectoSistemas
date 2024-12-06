import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import modelos.Administrativo;
import modelos.Usuario;

public class HiloDeCliente implements Runnable {
    private static ArrayList<HiloDeCliente> clientes = new ArrayList<>();
    private DataInputStream dataInput;
    private DataOutputStream dataOutput;
    private Usuario usuario;

    public HiloDeCliente(Socket socket, Usuario usuario) {
    
        this.usuario = usuario;
        System.out.println("Usuario: " + usuario);
        System.out.println("Clase: " + usuario.getClass().getName());
        try {
            dataInput = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            dataOutput = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        clientes.add(this);
        enviarListaUsuarios();
       
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
                            System.out.println("Guardando mensaje: " + mensajeParaGuardar);
    
                            guardarMensajePrivado(correoUsuario(), destinatario, mensajePrivado);
                            cliente.dataOutput.writeUTF("[Privado de " + correoUsuario() + "]: " + mensajePrivado);
                            break;
                        }
                    }
                } else if (mensaje.startsWith("/")) {
                    if(mensaje.startsWith("/Medico")){
                        String[] partes = mensaje.split(":", 2);
                        String destinatario = "Medico"; // Obtener destinatario
                        String mensajePrivado = partes[1];
                        for (HiloDeCliente cliente : clientes) {
                            if (cliente.correoUsuario().equals(destinatario)) {
                                String mensajeParaGuardar = "[Privado de " + correoUsuario() + "]: " + mensajePrivado;
                                guardarMensaje(mensajeParaGuardar);
                                cliente.dataOutput.writeUTF("[Privado de " + correoUsuario() + "]: " + mensajePrivado);
                                break;
                            }
                        }
                    }
                    String[] partes = mensaje.split(":", 2);
                    String destinatario = partes[0].substring(1); // Obtener destinatario
                    String mensajeGrupo = partes[1];

                    for (HiloDeCliente cliente : clientes) {
                        System.out.println("Cliente clase: "+cliente.usuario.getClass().getSimpleName());
                        System.out.println("Destinatario: "+destinatario);
                        System.out.println("Cliente: "+cliente.usuario);
                        if (cliente.usuario.getClass().getSimpleName().equals(destinatario)) {
                            System.out.println("Cliente2: "+cliente.usuario);
        
                            
                            if(destinatario.equals("Administrativo")){
                                Administrativo administrativo = (Administrativo) cliente.usuario;
                                System.out.println("Administrativo: "+ administrativo);
                                if(administrativo.esAuxiliar()){
                                    continue;
                                }
                            }
                            String mensajeParaGuardar = "[ "+ correoUsuario() +" para grupo de " + destinatario +"]: " + mensajeGrupo;
                            guardarMensaje(mensajeParaGuardar);
                            cliente.dataOutput.writeUTF("[ "+ correoUsuario() +" para grupo de " + destinatario +"]: " + mensajeGrupo);
                        }
                        else if(destinatario.equals("Auxiliar")){
                            if(cliente.usuario.getClass().getSimpleName().equals("Administrativo")){
                                Administrativo administrativo = (Administrativo) cliente.usuario;
                                if(administrativo.esAuxiliar()){
                                    String mensajeParaGuardar = "[ "+ correoUsuario() +" para grupo de " + destinatario +"]: " + mensajeGrupo;
                                    guardarMensaje(mensajeParaGuardar);
                                    cliente.dataOutput.writeUTF(mensajeParaGuardar);
                                }
                            }
                        }
                    }
                } else {
                    // Enviar mensaje general a todos
                    for (HiloDeCliente cliente : clientes) {

                        guardarMensaje(correoUsuario() + ": " + mensaje); // Guardar en archivo
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

    private void guardarHashMap(String remitente, String destinatario, String mensaje) {
        //guardar archivo con mensajes privados
        System.out.println("Guardando mensaje privado: " + remitente + " para " + destinatario + ": " + mensaje);
        try (FileWriter fw = new FileWriter("mensajesPrivados.txt", true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw)) {
            out.println(remitente + "," + destinatario + "," + mensaje);
        } catch (IOException e) {
            e.printStackTrace();
        }
    
    }

    private void guardarMensaje(String mensaje) {
        try (Connection connection = DatabaseConnectionCliente.getConnection()) {
            String query = "INSERT INTO mensaje (mensaje) VALUES (?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, mensaje);
            stmt.executeUpdate();

            System.out.println("Mensaje guardado en la base de datos: " + mensaje);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void guardarMensajePrivado(String remitente, String destinatario, String mensaje) {
        try (Connection connection = DatabaseConnectionCliente.getConnection()) {
            String query = "INSERT INTO mensajePrivado (remitente, destinatario, mensaje) VALUES (?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, remitente);
            stmt.setString(2, destinatario);
            stmt.setString(3, mensaje);
            stmt.executeUpdate();

            System.out.println("Mensaje privado guardado en la base de datos: " + remitente + " para " + destinatario + ": " + mensaje);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
