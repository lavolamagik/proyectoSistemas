package modelos;

public class Admin extends Usuario {

    public Admin(String nombre, String correo, String clave, String rut) {
        super(nombre, correo, clave, null);
    }

    @Override
    public String toString() {
        return "Admin: " + this.nombre + ", " + this.correo + ", " + this.clave;
    }
}
