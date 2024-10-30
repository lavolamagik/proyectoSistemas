import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.net.Socket;

public class ClienteAdministrativoGUI extends ClienteGUI {


    public ClienteAdministrativoGUI(Socket socket) {
        super(socket);
        try {
            dataOutput = new DataOutputStream(socket.getOutputStream());
            medicoListModel = new DefaultListModel<>();
            medicoList = new JList<>(medicoListModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        frame = new JFrame("Chat de Administrativo");
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
        JLabel label = new JLabel("Adminitrativos Disponibles");
        frame.add(label, BorderLayout.NORTH);
        administrativoListModel = new DefaultListModel<>();
        administrativoList = new JList<>(administrativoListModel);
        frame.add(new JScrollPane(administrativoList), BorderLayout.WEST);

        // Panel para los botones adicionales
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1));

        // Botón que dice comunicar con medico
        JButton comunicarMedico = new JButton("Comunicar con Medico");
        comunicarMedico.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(frame, new JScrollPane(medicoList), "Lista de Medicos", JOptionPane.INFORMATION_MESSAGE);
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
        String medicoSeleccionado = medicoList.getSelectedValue();
        String administrativoSeleccionado = administrativoList.getSelectedValue();
        
        if (medicoSeleccionado != null) {
            try {
                dataOutput.writeUTF("@" + medicoSeleccionado + ":" + mensaje); // Mensaje privado
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        else if (administrativoSeleccionado != null) {
            try {
                dataOutput.writeUTF("@" + administrativoSeleccionado + ":" + mensaje); // Mensaje privado
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } 
        else {
            try {
                dataOutput.writeUTF(mensaje); // Mensaje general
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        textField.setText("");
    }


}
