package modelos;

public class Perfil extends Usuario {
    String rut;

    public Perfil(String nombre, String rut, String correo, String clave) {
        super(nombre, correo, clave);
        this.rut = rut;
    }

    public String getRut() {
        return this.rut;
    }
}
