import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.net.Socket;

public class ClienteMedicoGUI extends ClienteGUI {

    public ClienteMedicoGUI(Socket socket) {
        super(socket);
        try {
            dataOutput = new DataOutputStream(socket.getOutputStream());
            administrativoListModel = new DefaultListModel<>();
            administrativoList = new JList<>(administrativoListModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        frame = new JFrame("Chat de Médico");
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
        JButton comunicarMedico = new JButton("Comunicar con Administrativo");
        comunicarMedico.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //comunicarConMedico();
            }
        });
        buttonPanel.add(comunicarMedico);

        // Botón que dice solicitar personal auxiliar
        JButton solicitarAuxiliar = new JButton("Solicitar Personal Auxiliar");
        solicitarAuxiliar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            //solicitarPersonalAuxiliar();
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
        
        textField.setText("");
    }
    
}
