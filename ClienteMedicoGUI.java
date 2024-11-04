import javax.swing.*;

import modelos.Usuario;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.net.Socket;

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
                enviarMensaje();
            }
        });
    }

    // Método para enviar un mensaje privado al usuario seleccionado
    private void enviarMensaje() {
        String mensaje = textField.getText();
        String usuarioSeleccionado = medicoList.getSelectedValue();
        
        if (usuarioSeleccionado != null) {
            try {
                dataOutput.writeUTF("@" + usuarioSeleccionado + ":" + mensaje); // Mensaje privado
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            try {
                dataOutput.writeUTF(mensaje); // Mensaje general
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        medicoList.clearSelection();
        textField.setText("");
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

    public void desplegarSolicitarPersonalAuxiliar() {
        JDialog dialogo = new JDialog(frame, "Solicitar Personal Auxiliar", true);
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
                    dataOutput.writeUTF("/Auxiliar:" + mensajeField.getText());
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

    
    
}
