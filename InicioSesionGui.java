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
    String perfil;

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
        String usuario = usuarioField.getText();
        String contrasena = new String(contrasenaField.getPassword());

        // Si el usuario y la contraseña son correctos, abrir la ventana correspondiente
        if (usuario.equals("admin") && contrasena.equals("admin")) {
            perfil = "Admin";
            //new ClienteAdminGUI(socket);
        } else if (usuario.equals("medico") && contrasena.equals("medico")) {
            perfil = "medico";
            //new ClienteMedicoGUI(socket);
        } else if (usuario.equals("administrativo") && contrasena.equals("administrativo")) {
            perfil = "administrativo";
            //new ClienteAdministrativoGUI(socket);
        } else {
            JOptionPane.showMessageDialog(frame, "Usuario o contraseña incorrectos", "Error", JOptionPane.ERROR_MESSAGE);
            perfil = "";
        }

        // Cerrar ventana después de iniciar sesión
        if (!perfil.isEmpty()) {
            frame.dispose();
        }

    }

    public String getPerfil() {
        return perfil;
    }
    
}
