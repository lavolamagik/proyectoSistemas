import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import modelos.Admin;
import modelos.Administrativo;
import modelos.Area;
import modelos.Medico;
import modelos.Usuario;

public class ServidorChat {
    private static final int PUERTO = 5000;
    private static ArrayList<HiloDeCliente> clientes = new ArrayList<>();
    private static ArrayList<Usuario> usuarios = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            System.out.println("Servidor iniciado...");
            while (true) {
                Socket socket = servidor.accept();
                if (socket != null && socket.isConnected()) {
                    Usuario usuario = obtenerUsuarioDeCliente(socket);
                    System.out.println("Usuario: " + usuario);
                    HiloDeCliente cliente = new HiloDeCliente(socket, usuario);
                    clientes.add(cliente);
                    new Thread(cliente).start();
                } else {
                    System.out.println("Error al aceptar la conexión del cliente.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Usuario obtenerUsuarioDeCliente(Socket socket) {
        try (Connection connection = DatabaseConnection.getConnection()) {
    
            DataInputStream dataInput = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream());
    
            while (true) {
                // Leer correo y clave enviados por el cliente
                String correo = dataInput.readUTF();
                String clave = dataInput.readUTF();
                System.out.println("Correo: " + correo);
                System.out.println("Clave: " + clave);

    
                // Consulta a la base de datos
                String query = "SELECT * FROM usuarios WHERE correo = ? AND clave = ?";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, correo);
                stmt.setString(2, clave);
                ResultSet rs = stmt.executeQuery();
    
                // If the user exists, create the corresponding object and send it
                if (rs.next()) {
                    String tipo = rs.getString("tipo");
                    String nombre = rs.getString("nombre");
                    String rut = rs.getString("rut");
                    String area = rs.getString("area");

                    // Crear el objeto de usuario según el tipo
                    Usuario usuario = null;
                    if ("Medico".equals(tipo)) {
                        usuario = new Medico(nombre, rut, correo, clave);
                    } else if ("Administrativo".equals(tipo)) {
                        usuario = new Administrativo(nombre, rut, correo, clave, Area.valueOf(area));
                    } else if ("Admin".equals(tipo)) {
                        usuario = new Admin(nombre, correo, clave);
                    }

                    // Enviar el usuario al cliente
                    if (usuario != null) {
                        dataOutput.writeUTF(usuario.toString());  // Enviar detalles del usuario autenticado
                        System.out.println("Usuario autenticado: " + usuario.getCorreo());
                        return usuario;
                    } else {
                        dataOutput.writeUTF("ERROR: Usuario no encontrado.");
                    }
                    
                } else {
                    dataOutput.writeUTF("ERROR: Usuario o contraseña incorrectos."); // Send error if no match
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Devuelve null si no se pudo autenticar
    }



    private static String usuarioToString(Usuario usuario) {
        if (usuario instanceof Medico) {
            Medico medico = (Medico) usuario;
            return "Medico," + medico.getNombre() + "," + medico.getRut() + "," + medico.getCorreo() + ","
                    + medico.getClave();
        } else if (usuario instanceof Administrativo) {
            Administrativo administrativo = (Administrativo) usuario;
            return "Administrativo," + administrativo.getNombre() + "," + administrativo.getRut() + ","
                    + administrativo.getCorreo() + ","
                    + administrativo.getClave() + "," + administrativo.getArea();
        } else if (usuario instanceof Admin) {
            Admin admin = (Admin) usuario;
            return "Admin," + admin.getNombre() + "," + admin.getCorreo() + "," + admin.getClave();
        }
        return "";
    }


}
