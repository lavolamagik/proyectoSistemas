import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import modelos.Admin;
import modelos.Usuario;
import modelos.Medico;
import modelos.Administrativo;
import modelos.Area;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InicioSesionGui {

    JFrame frame;
    JTextArea textArea;
    JTextField textField;
    JButton botonIniciarSesion;
    JTextField usuarioField;
    JPasswordField contrasenaField;
    DataOutputStream dataOutput;
    Usuario usuario;
    Usuario[] usuarios;

    public InicioSesionGui(Socket socket) {
        try {
            dataOutput = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // cargar usuarios
        usuarios = cargarUsuariosDesdeArchivo("c:\\Users\\rodri\\OneDrive\\Escritorio\\proyectoSistemas\\usuarios.txt");

        frame = new JFrame("Inicio de Sesión");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Panel de campos de entrada
        JPanel panelCampos = new JPanel(new GridLayout(2, 2));
        panelCampos.add(new JLabel("Usuario:"));
        usuarioField = new JTextField();
        panelCampos.add(usuarioField);

        panelCampos.add(new JLabel("Contraseña:"));
        contrasenaField = new JPasswordField();
        panelCampos.add(contrasenaField);

        // Panel del botón de inicio de sesión
        JPanel panelBoton = new JPanel();
        botonIniciarSesion = new JButton("Iniciar Sesión");
        panelBoton.add(botonIniciarSesion);

        // Añadir paneles al marco
        frame.add(panelCampos, BorderLayout.CENTER);
        frame.add(panelBoton, BorderLayout.SOUTH);

        frame.setVisible(true);

        // Acción del botón de inicio de sesión
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

        // Si el usuario y la contraseña son correctos, abrir la ventana correspondiente
        for (Usuario usuario : usuarios) {
            if (usuario.getCorreo().equals(correo) && usuario.getClave().equals(contrasena)) {
                this.usuario = usuario;
                break;
            }
        }

        // Cerrar ventana después de iniciar sesión
        if (this.usuario != null) {
            frame.dispose();
        } else {
            JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos");
        }

    }

    public Usuario getUsuario() {
        return usuario;
    }

    // Método para cargar usuarios desde el archivo
    private Usuario[] cargarUsuariosDesdeArchivo(String rutaArchivo) {
        ArrayList<Usuario> usuariosList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(": ");

                if (partes.length > 1) {
                    String tipo = partes[0];
                    String[] datos = partes[1].split(", ");

                    if (tipo.equals("Medico")) {
                        usuariosList.add(new Medico(datos[0], datos[1], datos[2], datos[3]));
                    } else if (tipo.equals("Administrativo")) {
                        usuariosList.add(
                                new Administrativo(datos[0], datos[1], datos[2], datos[3], Area.valueOf(datos[4])));
                    } else if (tipo.equals("Admin")) {
                        usuariosList.add(new Admin(datos[0], datos[1], datos[2]));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return usuariosList.toArray(new Usuario[0]);
    }

}
