import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.net.Socket;

public class ClienteChatGUI {
    private JFrame frame;
    private JTextArea textArea;
    private JTextField textField;
    private JButton sendButton;
    private JList<String> userList; // Lista para mostrar los usuarios conectados
    private DefaultListModel<String> userListModel;
    private DataOutputStream dataOutput;

    public ClienteChatGUI(Socket socket) {
        try {
            dataOutput = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        frame = new JFrame("Chat de Cliente");
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
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        frame.add(new JScrollPane(userList), BorderLayout.WEST);
        
        frame.setVisible(true);

        // Acción para enviar mensaje
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enviarMensaje();
            }
        });
    }

    // Método para agregar un usuario a la lista
    public void agregarUsuario(String usuario) {
        if (!userListModel.contains(usuario)) {
            userListModel.addElement(usuario);
        }
    }

    // Método para enviar un mensaje privado al usuario seleccionado
    private void enviarMensaje() {
        String mensaje = textField.getText();
        String usuarioSeleccionado = userList.getSelectedValue();
        
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
        
        textField.setText("");
    }

    // Método para mostrar mensajes en el área de texto
    public void mostrarMensaje(String mensaje) {
        textArea.append(mensaje + "\n");
    }
}
