import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.net.Socket;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import modelos.Usuario;

public class ClienteGUI {

    JFrame frame;
    JTextArea textArea;
    JTextField textField;
    JButton sendButton;
    JList<String> administrativoList; // Lista para mostrar los usuarios conectados
    JList<String> medicoList;
    DefaultListModel<String> administrativoListModel;
    DataOutputStream dataOutput;
    DefaultListModel<String> medicoListModel;
    Usuario usuario;

    public ClienteGUI(Socket socket, Usuario usuario) {
        try {
            dataOutput = new DataOutputStream(socket.getOutputStream());
            this.usuario = usuario;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para agregar un usuario a la lista
    public void agregarAdministrativo(String usuario) {
        if (!administrativoListModel.contains(usuario)) {
            administrativoListModel.addElement(usuario);
        }
    }
    
    // Método para mostrar mensajes en el área de texto
    public void mostrarMensaje(String mensaje) {
        textArea.append(mensaje + "\n");
    }

    public void limpiarListaUsuarios() {
        administrativoListModel.clear();
        medicoListModel.clear();
    }

    public void agregarMedico(String usuario) {
        if (!medicoListModel.contains(usuario)) {
            medicoListModel.addElement(usuario);
        }
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
