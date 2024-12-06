package modelos;

public class Administrativo extends Perfil {
    Area area;

    public Administrativo(String nombre, String rut, String correo, String clave, Area area) {
        super(nombre, rut, correo, clave);
        this.area = area;
    }

    public Area getArea() {
        return area;
    }

    public boolean esAuxiliar() {
        return this.area == Area.AUXILIAR;
    }

    public String getRut() {
        return this.rut;
    }

    @Override
    public String toString() {
        return "Administrativo: " + this.nombre + ", " + this.rut + ", " + this.correo + ", " + this.clave + ", "
                + this.area;
    }
}
