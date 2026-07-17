package desafio;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

public class App {

  
    static ArrayList<Estudiante> listaEstudiantes = new ArrayList<Estudiante>();

  

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        server.createContext("/estudiantes", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {

                String metodo = exchange.getRequestMethod();

                if (metodo.equals("GET")) {

                   
                    String respuesta = "[";
                    for (int i = 0; i < listaEstudiantes.size(); i++) {
                        Estudiante e = listaEstudiantes.get(i);
                        respuesta = respuesta + e.toJson();
                        if (i < listaEstudiantes.size() - 1) {
                            respuesta = respuesta + ",";
                        }
                    }
                    respuesta = respuesta + "]";

                    enviarRespuesta(exchange, 200, respuesta);

                } else if (metodo.equals("POST")) {

                    
                    Scanner sc = new Scanner(exchange.getRequestBody(), StandardCharsets.UTF_8);
                    sc.useDelimiter("\\A");
                    String body = "";
                    if (sc.hasNext()) {
                        body = sc.next();
                    }

                    String id = buscarValor(body, "id");
                    String nombre = buscarValor(body, "nombre");
                    String carrera = buscarValor(body, "carrera");

                    if (id == null || nombre == null || carrera == null) {
                        enviarRespuesta(exchange, 400, "{\"error\":\"faltan datos\"}");
                        return;
                    }

                    
                    boolean existe = false;
                    for (int i = 0; i < listaEstudiantes.size(); i++) {
                        if (listaEstudiantes.get(i).id.equals(id)) {
                            existe = true;
                        }
                    }

                    if (existe) {
                        enviarRespuesta(exchange, 409, "{\"error\":\"ese id ya existe\"}");
                        return;
                    }

                    Estudiante nuevo = new Estudiante(id, nombre, carrera);
                    listaEstudiantes.add(nuevo);

                    enviarRespuesta(exchange, 201, nuevo.toJson());

                } else {
                    enviarRespuesta(exchange, 405, "{\"error\":\"metodo no permitido\"}");
                }
            }
        });

        server.setExecutor(null);
        server.start();

        System.out.println("Servidor iniciado en http://localhost:8000/estudiantes");
    }

    
    static String buscarValor(String texto, String campo) {
        String buscar = "\"" + campo + "\"";
        int pos = texto.indexOf(buscar);

        if (pos == -1) {
            return null;
        }

        int dosPuntos = texto.indexOf(":", pos);
        int comilla1 = texto.indexOf("\"", dosPuntos + 1);
        int comilla2 = texto.indexOf("\"", comilla1 + 1);

        if (dosPuntos == -1 || comilla1 == -1 || comilla2 == -1) {
            return null;
        }

        return texto.substring(comilla1 + 1, comilla2);
    }


    static void enviarRespuesta(HttpExchange exchange, int codigo, String texto) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        byte[] bytes = texto.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(codigo, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
}