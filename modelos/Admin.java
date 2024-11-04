package modelos;

public class Admin extends Usuario {

    public Admin(String nombre, String correo, String clave) {
        super(nombre, correo, clave);
    }

    @Override
    public String toString() {
        return "Admin: " + this.nombre + ", " + this.correo + ", " + this.clave;
    }
}
