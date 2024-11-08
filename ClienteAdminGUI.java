import javax.swing.*;

import modelos.Admin;
import modelos.Administrativo;
import modelos.Area;
import modelos.Medico;
import modelos.Usuario;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.net.Socket;

public class ClienteAdminGUI extends ClienteGUI {

    public ClienteAdminGUI(Socket socket, Usuario usuario) {
        super(socket, usuario);
        try {
            dataOutput = new DataOutputStream(socket.getOutputStream());
            administrativoListModel = new DefaultListModel<>();
            administrativoList = new JList<>(administrativoListModel);
            medicoListModel = new DefaultListModel<>();
            medicoList = new JList<>(medicoListModel);
        } catch (Exception e) {
            e.printStackTrace();
        }

        frame = new JFrame("Usuario Administrador");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textArea = new JTextArea();
        textArea.setEditable(false);
        frame.add(new JScrollPane(textArea), BorderLayout.CENTER);

        JLabel emergencyLabel = new JLabel("Mensaje de Emergencia");
        frame.add(emergencyLabel, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        textField = new JTextField();
        sendButton = new JButton("Enviar");
        inputPanel.add(textField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        frame.add(inputPanel, BorderLayout.SOUTH);

        // Panel para los botones adicionales
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1));

        // Botón que dice comunicar con medico
        JButton monitorearConversaciones = new JButton("Monitorear conversaciones");
        monitorearConversaciones.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // monitorearConversaciones();
            }
        });
        buttonPanel.add(monitorearConversaciones);

        // Botón que dice comunicar con medico
        JButton registrarUsuario = new JButton("Registrar Usuario");
        registrarUsuario.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarFormularioCrearUsuario();
            }
        });
        buttonPanel.add(registrarUsuario);

        // Botón que dice solicitar personal auxiliar
        JButton reinciarClaveUsuario = new JButton("Reiniciar clave de usuario");
        reinciarClaveUsuario.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // reinciarClaveUsuario();
            }
        });
        buttonPanel.add(reinciarClaveUsuario);

        frame.add(buttonPanel, BorderLayout.WEST);
        frame.setVisible(true);

        // Acción para enviar mensaje
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enviarMensaje();
            }
        });
    }

    // Método para enviar un mensaje privado al usuario seleccionado
    private void enviarMensaje() {
        String mensaje = textField.getText();

        try {
            dataOutput.writeUTF(mensaje); // Mensaje general
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        textField.setText("");
    }

    public void mostrarFormularioCrearUsuario() {
        JDialog dialogo = new JDialog(frame, "Crear Nuevo Usuario", true);
        dialogo.setSize(400, 300);
        dialogo.setLayout(new GridLayout(6, 2));

        JTextField nombreField = new JTextField();
        JTextField rutField = new JTextField();
        JTextField correoField = new JTextField();
        JTextField claveField = new JTextField();
        JComboBox<String> perfilBox = new JComboBox<>(new String[] { "Medico", "Administrativo", "Admin" });
        JComboBox<String> areaBox = new JComboBox<>(new String[] { "Admisión", "Pabellón", "Exámenes", "Auxiliar" });

        dialogo.add(new JLabel("Nombre:"));
        dialogo.add(nombreField);
        dialogo.add(new JLabel("Correo:"));
        dialogo.add(correoField);
        dialogo.add(new JLabel("Clave:"));
        dialogo.add(claveField);
        dialogo.add(new JLabel("Perfil:"));
        dialogo.add(perfilBox);
        dialogo.add(new JLabel("Área (Solo Administrativo):"));
        dialogo.add(areaBox);

        JButton crearButton = new JButton("Crear");
        crearButton.addActionListener(e -> {
            String nombre = nombreField.getText();
            String correo = correoField.getText();
            String clave = claveField.getText();
            String perfil = (String) perfilBox.getSelectedItem();
            String area = (String) areaBox.getSelectedItem();

            // Crear usuario basado en el perfil seleccionado
            Usuario nuevoUsuario;
            if ("Medico".equals(perfil)) {
                nuevoUsuario = new Medico(nombre, "0", correo, clave);
            } else if ("Administrativo".equals(perfil)) {
                nuevoUsuario = new Administrativo(nombre, "0", correo, clave, Area.valueOf(area.toUpperCase()));
            } else {
                nuevoUsuario = new Admin(nombre, "0", correo);
            }

            // Agregar nuevo usuario al sistema
            ServidorChat.agregarUsuarioAlSistema(nuevoUsuario);
            dialogo.dispose();
        });

        dialogo.add(crearButton);
        dialogo.setLocationRelativeTo(frame);
        dialogo.setVisible(true);
    }

}
