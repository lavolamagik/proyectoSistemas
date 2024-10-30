import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.net.Socket;

public class ClienteAdminGUI  extends ClienteGUI {


    public ClienteAdminGUI(Socket socket) {
        super(socket);
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
                //monitorearConversaciones();
            }
        });
        buttonPanel.add(monitorearConversaciones);

        // Botón que dice comunicar con medico
        JButton registrarUsuario = new JButton("Registrar Usuario");
        registrarUsuario.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //registrarUsuario();
            }
        });
        buttonPanel.add(registrarUsuario);

        // Botón que dice solicitar personal auxiliar
        JButton reinciarClaveUsuario = new JButton("Reiniciar clave de usuario");
        reinciarClaveUsuario.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            //reinciarClaveUsuario();
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


}
