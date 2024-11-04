package modelos;

public class Administrativo extends Perfil{
    Area area;

    public Administrativo(String nombre, String rut, String correo, String clave, Area area) {
        super(nombre, rut, correo, clave);
        this.area = area;
    }

    @Override
    public String toString() {
        return "Administrativo: " + this.nombre + ", " + this.rut + ", " + this.correo + ", " + this.clave + ", " + this.area;
    }
}
