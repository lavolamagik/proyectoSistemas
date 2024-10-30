import java.io.DataOutputStream;
import java.net.Socket;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JTextField;

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

    public ClienteGUI(Socket socket) {
        try {
            dataOutput = new DataOutputStream(socket.getOutputStream());
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
}
