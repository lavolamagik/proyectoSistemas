package modelos;

public class Usuario {
    String nombre;
    String correo;
    String clave;
    String rut;

    public Usuario(String nombre, String correo, String clave, String rut) {
        this.nombre = nombre;
        this.correo = correo;
        this.clave = clave;
        this.rut = rut;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getRut() {
        return rut;
    }

    @Override
    public String toString() {
        return nombre + ", " + correo + ", " + clave;
    }
}
