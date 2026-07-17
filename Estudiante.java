package desafio;

public class Estudiante {

    public String id;
    public String nombre;
    public String carrera;

    public Estudiante(String id, String nombre, String carrera) {
        this.id = id;
        this.nombre = nombre;
        this.carrera = carrera;
    }

    public String toJson() {
        String json = "{";
        json = json + "\"id\":\"" + id + "\",";
        json = json + "\"nombre\":\"" + nombre + "\",";
        json = json + "\"carrera\":\"" + carrera + "\"";
        json = json + "}";
        return json;
    }
}