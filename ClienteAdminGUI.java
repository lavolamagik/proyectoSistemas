import javax.swing.*;

import modelos.Admin;
import modelos.Administrativo;
import modelos.Area;
import modelos.Medico;
import modelos.Usuario;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

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
                mostrarFormularioCambioClave();
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
        dialogo.setLayout(new GridLayout(10, 1));

        JTextField nombreField = new JTextField();
        JTextField rutField = new JTextField();
        JTextField correoField = new JTextField();
        JTextField claveField = new JTextField();
        JComboBox<String> perfilBox = new JComboBox<>(new String[] { "Medico", "Administrativo", "Admin" });
        JComboBox<String> areaBox = new JComboBox<>(new String[] { "ADMISION", "PABELLON", "EXAMENES", "AUXILIAR" });

        dialogo.add(new JLabel("Nombre:"));
        dialogo.add(nombreField);
        dialogo.add(new JLabel("Rut:"));
        dialogo.add(rutField);
        dialogo.add(new JLabel("Correo:"));
        dialogo.add(correoField);
        dialogo.add(new JLabel("Clave:"));
        dialogo.add(claveField);
        dialogo.add(new JLabel("Perfil:"));
        dialogo.add(perfilBox);
        dialogo.add(new JLabel("Área (Solo Administrativo):"));
        dialogo.add(areaBox);
        perfilBox.addActionListener(e -> {
            String perfilSeleccionado = (String) perfilBox.getSelectedItem();
            areaBox.setEnabled("Administrativo".equals(perfilSeleccionado));
        });
        JButton crearButton = new JButton("Crear");
        crearButton.addActionListener(e -> {
            String nombre = nombreField.getText();
            String correo = correoField.getText();
            String rut = rutField.getText();
            String clave = claveField.getText();
            String perfil = (String) perfilBox.getSelectedItem();
            String area = (String) areaBox.getSelectedItem();

            // Crear usuario basado en el perfil seleccionado
            Usuario nuevoUsuario;
            if ("Medico".equals(perfil)) {
                nuevoUsuario = new Medico(nombre, rut, correo, clave);
            } else if ("Administrativo".equals(perfil)) {
                nuevoUsuario = new Administrativo(nombre, rut, correo, clave, Area.valueOf(area.toUpperCase()));
            } else {
                nuevoUsuario = new Admin(nombre, correo, clave, rut);
            }

            // Agregar nuevo usuario al sistema
            ServidorChat.agregarUsuarioAlSistema(nuevoUsuario);
            dialogo.dispose();
        });

        dialogo.add(crearButton);
        dialogo.setLocationRelativeTo(frame);
        dialogo.setVisible(true);
    }

    private void mostrarFormularioCambioClave() {
        JDialog dialogo = new JDialog(frame, "Cambiar Contraseña de Usuario", true);
        dialogo.setSize(300, 200);
        dialogo.setLayout(new GridLayout(3, 2));

        JTextField correoField = new JTextField();
        JTextField nuevaClaveField = new JTextField();

        dialogo.add(new JLabel("Correo del Usuario:"));
        dialogo.add(correoField);
        dialogo.add(new JLabel("Nueva Contraseña:"));
        dialogo.add(nuevaClaveField);

        JButton confirmarButton = new JButton("Confirmar");
        confirmarButton.addActionListener(event -> {
            String correo = correoField.getText();
            String nuevaClave = nuevaClaveField.getText();

            if (!correo.isEmpty() && !nuevaClave.isEmpty()) {
                boolean exito = cambiarClaveUsuario(correo, nuevaClave);
                if (exito) {
                    JOptionPane.showMessageDialog(dialogo, "Contraseña actualizada con éxito.");
                    dialogo.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialogo, "Usuario no encontrado.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(dialogo, "Por favor, complete todos los campos.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        dialogo.add(confirmarButton);
        dialogo.setLocationRelativeTo(frame);
        dialogo.setVisible(true);
    }

    private boolean cambiarClaveUsuario(String correo, String nuevaClave) {
        ArrayList<Usuario> usuariosList = cargarUsuariosDesdeArchivo("usuarios.csv");
        boolean encontrado = false;

        for (Usuario usuario : usuariosList) {
            if (usuario.getCorreo().equals(correo)) {
                usuario.setClave(nuevaClave);
                encontrado = true;
                break;
            }
        }

        if (encontrado) {
            actualizarArchivoUsuarios(usuariosList);
        }

        return encontrado;
    }

    private ArrayList<Usuario> cargarUsuariosDesdeArchivo(String rutaArchivo) {
        ArrayList<Usuario> usuariosList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(",");

                if (partes.length > 1) {
                    String tipo = partes[0];

                    if (tipo.equals("Medico")) {
                        usuariosList.add(new Medico(partes[1], partes[2], partes[3], partes[4]));
                    } else if (tipo.equals("Administrativo")) {
                        usuariosList.add(new Administrativo(partes[2], partes[2], partes[3], partes[4],
                                Area.valueOf(partes[5])));
                    } else if (tipo.equals("Admin")) {
                        usuariosList.add(new Admin(partes[1], partes[2], partes[3], partes[4]));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return usuariosList;
    }

    private void actualizarArchivoUsuarios(ArrayList<Usuario> usuariosList) {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter("usuarios.csv"))) {
            for (Usuario usuario : usuariosList) {
                writer.write(usuarioToString(usuario));
                writer.newLine();
            }
        } catch (IOException e) {
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
