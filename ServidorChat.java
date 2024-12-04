import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import modelos.Admin;
import modelos.Administrativo;
import modelos.Area;
import modelos.Medico;
import modelos.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ServidorChat {
    private static final int PUERTO = 5000;
    private static ArrayList<HiloDeCliente> clientes = new ArrayList<>();
    private static ArrayList<Usuario> usuarios = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            System.out.println("Servidor iniciado...");
            while (true) {
                Socket socket = servidor.accept();
                Usuario usuario = obtenerUsuarioDeCliente(socket);
                HiloDeCliente cliente = new HiloDeCliente(socket, usuario);
                clientes.add(cliente);
                new Thread(cliente).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Usuario obtenerUsuarioDeCliente(Socket socket) {
        try (Connection connection = DatabaseConnection.getConnection();
                DataInputStream dataInput = new DataInputStream(socket.getInputStream())) {

            // Leer correo y clave enviados por el cliente
            String correo = dataInput.readUTF();
            String clave = dataInput.readUTF();

            // Consulta a la base de datos
            String query = "SELECT * FROM usuarios WHERE correo = ? AND clave = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, correo);
            stmt.setString(2, clave);
            ResultSet rs = stmt.executeQuery();

            // Si el usuario existe, crear el objeto correspondiente
            if (rs.next()) {
                String tipo = rs.getString("tipo");
                String nombre = rs.getString("nombre");
                String rut = rs.getString("rut");
                String area = rs.getString("area");

                if ("Medico".equals(tipo)) {
                    return new Medico(nombre, rut, correo, clave);
                } else if ("Administrativo".equals(tipo)) {
                    return new Administrativo(nombre, rut, correo, clave, Area.valueOf(area));
                } else if ("Admin".equals(tipo)) {
                    return new Admin(nombre, correo, clave, null);
                }
            }
            if (!rs.next()) {
                DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream());
                dataOutput.writeUTF("ERROR: Usuario o contraseña incorrectos.");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void agregarUsuarioAlSistema(Usuario usuario) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO usuarios (tipo, nombre, rut, correo, clave, area) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, usuario.getClass().getSimpleName()); // 'Medico', 'Administrativo', o 'Admin'
            stmt.setString(2, usuario.getNombre());
            stmt.setString(3, usuario instanceof Medico || usuario instanceof Administrativo ? usuario.getRut() : null);
            stmt.setString(4, usuario.getCorreo());
            stmt.setString(5, usuario.getClave());
            stmt.setString(6,
                    usuario instanceof Administrativo ? ((Administrativo) usuario).getArea().toString() : null);
            stmt.executeUpdate();

            System.out.println("Usuario agregado a la base de datos: " + usuario.getCorreo());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
