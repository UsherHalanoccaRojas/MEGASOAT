package Services;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import com.google.gson.Gson;
import java.util.Map;
import Services.ChatService;
import Modelo.DAO.PuntoVentaDAO;
import Modelo.Entidades.PuntoVenta;

public class ChatHttpServer {
    private ChatService chatService = new ChatService();
    private PuntoVentaDAO pvDAO = new PuntoVentaDAO();
    
    
    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/chat/recibir", new RecibirHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Servidor HTTP escuchando en http://localhost:8080/api/chat/recibir");
    }

    class RecibirHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

                Gson gson = new Gson();
                Map<String, String> payload = gson.fromJson(body, Map.class);

                String telefono = payload.get("telefono");
                String contenido = payload.get("contenido");
                String tipo = payload.getOrDefault("tipo", "texto");

                // Aquí va tu bloque
                PuntoVenta pv = pvDAO.findByTelefono(telefono);
                boolean ok = false;
                if (pv != null) {
                    ok = chatService.recibirMensaje(pv.getId(), contenido, tipo);
                }

                String response = "{\"success\":" + ok + "}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } else {
                exchange.sendResponseHeaders(405, -1); // Método no permitido
            }
        }
    }
}
