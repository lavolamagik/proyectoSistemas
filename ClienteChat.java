import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClienteChat {
    private ClienteGUI gui;
    private Socket socket;
    private DataInputStream dataInput;
    private DataOutputStream dataOutput;
    private String perfil;

    public ClienteChat() {
        try {
            socket = new Socket("localhost", 5000);
            dataInput = new DataInputStream(socket.getInputStream());
            dataOutput = new DataOutputStream(socket.getOutputStream());
            

            //this.perfil = perfil;
            
            InicioSesionGui inicioSesion = new InicioSesionGui(socket);
            //recibir perfil de InicioSesionGui
            while (inicioSesion.getPerfil() == null || inicioSesion.getPerfil().isEmpty()) {
                Thread.sleep(100);  // Esperar brevemente
            }
            perfil = inicioSesion.getPerfil();

            if(this.perfil.equals("medico")){
                gui = new ClienteMedicoGUI(socket);
            }
            else if (this.perfil.equals("administrativo")){
                gui = new ClienteAdministrativoGUI(socket);   
            }
            else if(this.perfil.equals("Admin")){
                gui = new ClienteAdminGUI(socket);
            }
                


            dataOutput.writeUTF(perfil);

            // Hilo para recibir mensajes
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            String mensaje = dataInput.readUTF();

                            if (mensaje.startsWith("#usuarios:")) {
                                gui.limpiarListaUsuarios(); // Limpia la lista antes de agregar
                                System.out.println(mensaje);
                                String[] usuarios = mensaje.split(":")[1].split(";");
                                for (String usuario : usuarios) {
                                    String[] partes = usuario.split(","); // Dividir nombre y perfil
                                    if ( partes[1].equals("administrativo")) {
                                        gui.agregarAdministrativo(partes[0]); 
                                    }
                                    else if(partes[1].equals("medico")){
                                        gui.agregarMedico(partes[0]);
                                    }
                                    
                                }
                            } else {
                                gui.mostrarMensaje(mensaje);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    
    
    }

    public static void main(String[] args) {
        new ClienteChat();

    }
}
