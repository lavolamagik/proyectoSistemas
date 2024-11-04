import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

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

    public InicioSesionGui(Socket socket) {
        try {
            dataOutput = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        Usuario[] usuarios = new Usuario[6];
        usuarios[0] = new Admin("admin", "admin", "admin");
        usuarios[1] = new Medico("medico", "0", "medico@correo.com", "12345");
        usuarios[2] = new Medico("medico2", "0", "medico2@correo.com", "12345");
        usuarios[3] = new Medico("medico3", "0", "medico3@correo.com", "12345");
        usuarios[4] = new Administrativo("administrativo", "0", "administrativo@correo.com", "12345", Area.ADMISION);
        usuarios[5] = new Administrativo("administrativo2", "0", "administrativo2@correo.com", "12345", Area.ADMISION);

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
        }

    }

    public Usuario getUsuario() {
        return usuario;
    }

}
