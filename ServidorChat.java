import java.io.BufferedWriter;
import java.io.DataInputStream;
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
        try {
            DataInputStream dataInput = new DataInputStream(socket.getInputStream());
            String usuarioString = dataInput.readUTF(); // Lee el perfil enviado por el cliente
            String[] partes = usuarioString.split(": ");
            if (partes[0].equals("Medico")) {
                String[] datos = partes[1].split(", ");
                return new Medico(datos[0], datos[1], datos[2], datos[3]);
            } else if (partes[0].equals("Administrativo")) {
                String[] datos = partes[1].split(", ");
                return new Administrativo(datos[0], datos[1], datos[2], datos[3], Area.valueOf(datos[4]));
            } else if (partes[0].equals("Admin")) {
                String[] datos = partes[1].split(", ");
                return new Admin(datos[0], datos[1], datos[2]);
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void agregarUsuarioAlSistema(Usuario usuario) {
        // Agregar el usuario a la lista de usuarios del sistema
        usuarios.add(usuario);

        // Guardar el usuario en un archivo CSV para persistencia
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("usuarios.csv", true))) {
            writer.write(usuarioToString(usuario));
            writer.newLine();
            System.out.println("Usuario agregado y guardado: " + usuario.getCorreo());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String usuarioToString(Usuario usuario) {
        if (usuario instanceof Medico) {
            Medico medico = (Medico) usuario;
            return "Medico," + medico.getNombre() + "," + medico.getRut() +"," + medico.getCorreo() +"," + medico.getClave();
        } else if (usuario instanceof Administrativo) {
            Administrativo administrativo = (Administrativo) usuario;
            return "Administrativo," + administrativo.getNombre() + "," + administrativo.getRut() +"," + administrativo.getCorreo() + ","
                    + administrativo.getClave() + "," + administrativo.getArea();
        } else if (usuario instanceof Admin) {
            Admin admin = (Admin) usuario;
            return "Admin," + admin.getNombre() + "," + admin.getCorreo() + "," + admin.getClave();
        }
        return "";
    }

}
