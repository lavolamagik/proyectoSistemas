import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.HashMap;

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
    HashMap<String, JTextArea> chatPrivados = new HashMap<>();

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
        if(mensaje.startsWith("[Privado de")){
            String[] mensajeArray = mensaje.split(":");
            String usuario = mensajeArray[0].substring(12, mensajeArray[0].length()-1);
            System.out.println("Usuario: " + usuario);
            String mensajePrivado = mensajeArray[1];
            if (!chatPrivados.containsKey(usuario)) {
                chatPrivados.put(usuario, new JTextArea());
            }
            chatPrivados.get(usuario).append("[" + usuario + "]: " + mensajePrivado + "\n");
            return;
        }


        textArea.append(mensaje + "\n");
    }

    public void limpiarListaUsuarios() {
        administrativoListModel.clear();
        medicoListModel.clear();
    }

    public void agregarMedico(String usuario) {
        if (!medicoListModel.contains(usuario)) {
            medicoListModel.addElement(usuario);
            System.out.println("Agregado: " + usuario);
            chatPrivados.put(usuario, new JTextArea());
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
