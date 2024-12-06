import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.swing.*;
import modelos.Admin;
import modelos.Administrativo;
import modelos.Area;
import modelos.Medico;
import modelos.Usuario;

public class InicioSesionGui {

    JFrame frame;
    JTextField usuarioField;
    JPasswordField contrasenaField;
    JButton botonIniciarSesion;
    DataOutputStream dataOutput;
    DataInputStream dataInput;
    Usuario usuario;
    Usuario[] usuarios;

    public InicioSesionGui(Socket socket) {
        try {
            dataOutput = new DataOutputStream(socket.getOutputStream());
            dataInput = new DataInputStream(socket.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Load users from CSV
        //usuarios = cargarUsuariosDesdeBaseDeDatos();

        frame = new JFrame("Inicio de Sesión");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Input panel
        JPanel panelCampos = new JPanel(new GridLayout(2, 2));
        panelCampos.add(new JLabel("Usuario:"));
        usuarioField = new JTextField();
        panelCampos.add(usuarioField);

        panelCampos.add(new JLabel("Contraseña:"));
        contrasenaField = new JPasswordField();
        panelCampos.add(contrasenaField);

        // Login button panel
        JPanel panelBoton = new JPanel();
        botonIniciarSesion = new JButton("Iniciar Sesión");
        panelBoton.add(botonIniciarSesion);

        // Add panels to frame
        frame.add(panelCampos, BorderLayout.CENTER);
        frame.add(panelBoton, BorderLayout.SOUTH);
        frame.setVisible(true);

        // Login button action
        botonIniciarSesion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iniciarSesion(socket);
            }
        });
    }

    private void iniciarSesion(Socket socket) {
        String correo = usuarioField.getText();
        String contrasena = new String(contrasenaField.getPassword());

        try {
            // Send credentials to the server
            dataOutput.writeUTF(correo);
            dataOutput.writeUTF(contrasena);

            // Receive the response from the server
            String response = dataInput.readUTF();

            System.out.println(response);
            // If response is the user information (not an error), we assign the user
            if (!response.equals("ERROR: Usuario o contraseña incorrectos.")) {
                usuario = parseUsuarioFromString(response);
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(null, response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to parse the user data received as a string from the server
    private Usuario parseUsuarioFromString(String data) {
        String[] partes = data.split(": ");
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
    }
    
    public Usuario getUsuario() {
        return usuario;
    }

    private Usuario[] cargarUsuariosDesdeBaseDeDatos() {
        ArrayList<Usuario> usuariosList = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM usuarios";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String tipo = rs.getString("tipo");
                String nombre = rs.getString("nombre");
                String rut = rs.getString("rut");
                String correo = rs.getString("correo");
                String clave = rs.getString("clave");
                String area = rs.getString("area");

                switch (tipo) {
                    case "Medico":
                        usuariosList.add(new Medico(nombre, rut, correo, clave));
                        break;
                    case "Administrativo":
                        usuariosList.add(new Administrativo(nombre, rut, correo, clave, Area.valueOf(area)));
                        break;
                    case "Admin":
                        usuariosList.add(new Admin(nombre, correo, clave));
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return usuariosList.toArray(new Usuario[0]);
    }
}
