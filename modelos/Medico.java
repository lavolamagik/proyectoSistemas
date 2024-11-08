package modelos;

public class Medico extends Perfil {

    public Medico(String nombre, String rut, String correo, String clave) {
        super(nombre, rut, correo, clave);
    }

    public String getRut() {
        return this.rut;
    }

    @Override
    public String toString() {
        return "Medico: " + this.nombre + ", " + this.rut + ", " + this.correo + ", " + this.clave;
    }
}
