import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
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
        usuarios = cargarUsuariosDesdeCSV("usuarios.csv");

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

    // Method to load users from CSV
    private Usuario[] cargarUsuariosDesdeCSV(String rutaArchivo) {
        ArrayList<Usuario> usuariosList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(",");

                String tipo = datos[0];
                System.out.println(tipo);
                switch (tipo) {
                    case "Medico":
                        usuariosList.add(new Medico(datos[1], datos[2], datos[3], datos[4]));
                        break;
                    case "Administrativo":
                        usuariosList.add(new Administrativo(datos[1], datos[2], datos[3], datos[4], Area.valueOf(datos[5])));
                        break;
                    case "Admin":
                        usuariosList.add(new Admin(datos[1], datos[2], datos[3]));
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return usuariosList.toArray(new Usuario[0]);
    }
}
