import javax.swing.*;

import java.awt.event.MouseEvent;

import modelos.Usuario;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.awt.event.MouseAdapter;

public class ClienteMedicoGUI extends ClienteGUI {

    public ClienteMedicoGUI(Socket socket, Usuario usuario) {
        super(socket, usuario);
        try {
            dataOutput = new DataOutputStream(socket.getOutputStream());
            administrativoListModel = new DefaultListModel<>();
            administrativoList = new JList<>(administrativoListModel);
        } catch (Exception e) {
            e.printStackTrace();
        }

        frame = new JFrame("Chat de " + usuario.getNombre());
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textArea = new JTextArea();
        textArea.setEditable(false);
        frame.add(new JScrollPane(textArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        textField = new JTextField();
        sendButton = new JButton("Enviar");
        inputPanel.add(textField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        frame.add(inputPanel, BorderLayout.SOUTH);

        // Configurar la lista de usuarios
        JLabel label = new JLabel("Medicos Disponibles");
        frame.add(label, BorderLayout.NORTH);
        medicoListModel = new DefaultListModel<>();
        medicoList = new JList<>(medicoListModel);
        frame.add(new JScrollPane(medicoList), BorderLayout.WEST);

        // Panel para los botones adicionales
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1));

        // Botón que dice comunicar con medico
        JButton comunicarAdministrativo = new JButton("Comunicar con Administrativo");
        comunicarAdministrativo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                desplegarcComunicarAdministrativo();
            }
        });
        buttonPanel.add(comunicarAdministrativo);

        // Botón que dice solicitar personal auxiliar
        JButton solicitarAuxiliar = new JButton("Solicitar Personal Auxiliar");
        solicitarAuxiliar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                desplegarSolicitarPersonalAuxiliar();
            }
        });
        buttonPanel.add(solicitarAuxiliar);

        frame.add(buttonPanel, BorderLayout.EAST);
        frame.setVisible(true);

        // Acción para enviar mensaje
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enviarMensajeGeneral();
            }
        });

        // Doble clic en un usuario para abrir ventana de chat privado
        medicoList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String usuarioSeleccionado = medicoList.getSelectedValue();
                    if (usuarioSeleccionado != null) {
                        abrirVentanaChatPrivado(usuarioSeleccionado);
                    }
                }
            }
        });
    }

    public void desplegarcComunicarAdministrativo() {
        JDialog dialogo = new JDialog(frame, "Comunicar con Administrativo", true);
        dialogo.setSize(300, 150);
        dialogo.setLayout(new BorderLayout());

        JTextField mensajeField = new JTextField();
        JButton enviarButton = new JButton("Enviar");

        dialogo.add(mensajeField, BorderLayout.CENTER);
        dialogo.add(enviarButton, BorderLayout.SOUTH);

        enviarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dataOutput.writeUTF("/Administrativo:" + mensajeField.getText());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                mensajeField.setText("");
                dialogo.dispose(); // Cierra el diálogo después de enviar el mensaje
            }
        });

        dialogo.setLocationRelativeTo(frame); // Centra el diálogo en la ventana principal
        dialogo.setVisible(true); // Muestra el diálogo

    }

    // Método para enviar un mensaje general
    private void enviarMensajeGeneral() {
        String mensaje = textField.getText();
        if (!mensaje.isEmpty()) {
            try {
                dataOutput.writeUTF("/Medico:" + mensaje); // Enviar mensaje general
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            textField.setText("");
        }
    }

    // Método para abrir una ventana de chat privado
    public void abrirVentanaChatPrivado(String usuarioSeleccionado) {
        
        cargarChatPrivado();

        JFrame ventanaChatPrivado = new JFrame("Chat con " + usuarioSeleccionado);
        ventanaChatPrivado.setSize(400, 300);
        ventanaChatPrivado.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        if (!chatPrivados.containsKey(usuarioSeleccionado)) {
            chatPrivados.put(usuarioSeleccionado, new JTextArea());
        }
        JTextArea areaMensajes = chatPrivados.get(usuarioSeleccionado);

        areaMensajes.setEditable(false);
        ventanaChatPrivado.add(new JScrollPane(areaMensajes), BorderLayout.CENTER);

        JTextField campoMensajePrivado = new JTextField();
        JButton botonEnviarPrivado = new JButton("Enviar");
        JButton botonLimpiar = new JButton("Limpiar");

        JPanel panelEntrada = new JPanel(new BorderLayout());
        panelEntrada.add(campoMensajePrivado, BorderLayout.CENTER);
        panelEntrada.add(botonEnviarPrivado, BorderLayout.EAST);
    
        // Agregar botón Limpiar al panel
        JPanel panelBotones = new JPanel(new BorderLayout());
        panelBotones.add(botonEnviarPrivado, BorderLayout.EAST);
        panelBotones.add(botonLimpiar, BorderLayout.WEST);
    
        panelEntrada.add(panelBotones, BorderLayout.EAST);
        ventanaChatPrivado.add(panelEntrada, BorderLayout.SOUTH);

        botonEnviarPrivado.addActionListener(e -> {
            String mensaje = campoMensajePrivado.getText().trim();
            if (!mensaje.isEmpty()) {
                try {
                    dataOutput.writeUTF("@" + usuarioSeleccionado + ":" + mensaje); // Enviar mensaje privado
                    areaMensajes.append("Tú: " + mensaje + "\n"); // Mostrar en la ventana privada
                    campoMensajePrivado.setText("");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        botonLimpiar.addActionListener(e -> {
            areaMensajes.setText(""); // Limpiar el área de mensajes
            chatPrivados.put(usuarioSeleccionado, new JTextArea()); // Limpiar el chat privado
        });

        ventanaChatPrivado.setLocationRelativeTo(frame); // Centra la ventana de chat privado en la ventana principal
        ventanaChatPrivado.setVisible(true);
    }

    public void cargarChatPrivado() {
        try (Connection connection = DatabaseConnectionCliente.getConnection()) {
            String query = "SELECT * FROM mensajePrivado WHERE (remitente = ?) OR (destinatario = ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, usuario.getCorreo());
            stmt.setString(2, usuario.getCorreo());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String remitente = rs.getString("remitente");
                String destinatario = rs.getString("destinatario");
                String mensaje = rs.getString("mensaje");

                if (remitente.equals(usuario.getCorreo())) {
                    if (!chatPrivados.containsKey(destinatario)) {
                        chatPrivados.put(destinatario, new JTextArea());
                    }
                    chatPrivados.get(destinatario).append("Tú: " + mensaje + "\n");
                } else {
                    if (!chatPrivados.containsKey(remitente)) {
                        chatPrivados.put(remitente, new JTextArea());
                    }
                    chatPrivados.get(remitente).append(remitente + ": " + mensaje + "\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        

    }

}
