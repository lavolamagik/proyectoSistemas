import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.swing.*;
import modelos.Admin;
import modelos.Usuario;
import modelos.Medico;
import modelos.Administrativo;
import modelos.Area;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InicioSesionGui {

    JFrame frame;
    JTextField usuarioField;
    JPasswordField contrasenaField;
    JButton botonIniciarSesion;
    DataOutputStream dataOutput;
    Usuario usuario;
    Usuario[] usuarios;

    public InicioSesionGui(Socket socket) {
        try {
            dataOutput = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Load users from CSV
        usuarios = cargarUsuariosDesdeBaseDeDatos();

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
                iniciarSesion();
            }
        });
    }

    private void iniciarSesion() {
        String correo = usuarioField.getText();
        String contrasena = new String(contrasenaField.getPassword());

        // Check credentials
        for (Usuario usuario : usuarios) {
            if (usuario.getCorreo().equals(correo) && usuario.getClave().equals(contrasena)) {
                this.usuario = usuario;
                break;
            }
        }

        if (this.usuario != null) {
            frame.dispose();
        } else {
            JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos");
        }
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
                        usuariosList.add(new Admin(nombre, correo, clave, null));
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return usuariosList.toArray(new Usuario[0]);
    }
}
