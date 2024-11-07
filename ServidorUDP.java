import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ServidorUDP {
    private static final int PUERTO = 3003; // Puerto donde el servidor va a escuchar
    private static final List<Pregunta> preguntas = new ArrayList<>();

    public static void main(String[] args) {
        // Crear algunas preguntas
        preguntas.add(new Pregunta("¿Cuál es la capital de Brasil?", "sao paulo"));
        preguntas.add(new Pregunta("¿Cuál es el idioma oficial de Francia?", "frances"));
        preguntas.add(new Pregunta("¿Cuántos estados tiene EEUU?", "50"));
        preguntas.add(new Pregunta("¿Cuál es el animal más rápido terrestre?", "guepardo"));
        preguntas.add(new Pregunta("¿Cuál es el océano más grande?", "pacifico"));

        try (DatagramSocket socket = new DatagramSocket(PUERTO)) {
            System.out.println("Servidor UDP escuchando en el puerto " + PUERTO);

            while (true) {
                // Esperar un paquete del cliente
                byte[] buffer = new byte[1024];
                DatagramPacket packetRecibido = new DatagramPacket(buffer, buffer.length);
                socket.receive(packetRecibido);  // Recibir mensaje inicial del cliente

                String mensaje = new String(packetRecibido.getData(), 0, packetRecibido.getLength());
                System.out.println("Mensaje recibido: " + mensaje);

                // Si el cliente está listo para el test, comenzamos a enviar preguntas
                if (mensaje.equalsIgnoreCase("¿Estás listo para el test?")) {
                    // Enviar preguntas al cliente y recibir respuestas
                    Collections.shuffle(preguntas); // Mezcla las preguntas
                    int puntajeTotal = 0;

                    for (Pregunta pregunta : preguntas) {
                        // Enviar la pregunta
                        mensaje = "Pregunta: " + pregunta.getPregunta();
                        buffer = mensaje.getBytes();
                        DatagramPacket packetPregunta = new DatagramPacket(buffer, buffer.length, packetRecibido.getAddress(), packetRecibido.getPort());
                        socket.send(packetPregunta);  // Enviar pregunta al cliente

                        // Recibir respuesta del cliente
                        buffer = new byte[1024];
                        DatagramPacket respuestaPacket = new DatagramPacket(buffer, buffer.length);
                        socket.receive(respuestaPacket);  // Recibe la respuesta del cliente
                        String respuesta = new String(respuestaPacket.getData(), 0, respuestaPacket.getLength()).trim();

                        // Evaluar la respuesta
                        boolean esCorrecta = pregunta.esCorrecta(respuesta);
                        String resultado = esCorrecta ? "Correcto" : "Incorrecto";

                        // Imprimir el resultado en la terminal del servidor
                        System.out.println("Pregunta: " + pregunta.getPregunta());
                        System.out.println("Respuesta del cliente: " + respuesta);
                        System.out.println("Resultado: " + resultado);

                        // Enviar el resultado al cliente después de cada respuesta
                        buffer = resultado.getBytes();
                        DatagramPacket packetResultado = new DatagramPacket(buffer, buffer.length, packetRecibido.getAddress(), packetRecibido.getPort());
                        socket.send(packetResultado);  // Enviar el resultado al cliente

                        // Registrar la pregunta y la respuesta en el archivo
                        RegistroServidor.registrarPreguntaYRespuesta(pregunta.getPregunta(), respuesta, resultado);

                        // Sumar los puntos (4 puntos por respuesta correcta)
                        if (esCorrecta) {
                            puntajeTotal += 4;
                        }
                    }

                    // Enviar puntaje final al cliente (sobre 20)
                    mensaje = "Tu puntaje final es: " + puntajeTotal + "/20";
                    buffer = mensaje.getBytes();
                    DatagramPacket packetPuntaje = new DatagramPacket(buffer, buffer.length, packetRecibido.getAddress(), packetRecibido.getPort());
                    socket.send(packetPuntaje);  // Enviar puntaje final al cliente

                    // Enviar mensaje de despedida al cliente
                    mensaje = "Gracias por participar. Conexión terminada.";
                    buffer = mensaje.getBytes();
                    DatagramPacket packetDespedida = new DatagramPacket(buffer, buffer.length, packetRecibido.getAddress(), packetRecibido.getPort());
                    socket.send(packetDespedida);  // Enviar despedida al cliente
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Clase interna para la pregunta y la validación
    static class Pregunta {
        private String pregunta;
        private String respuestaCorrecta;

        public Pregunta(String pregunta, String respuestaCorrecta) {
            this.pregunta = pregunta;
            this.respuestaCorrecta = respuestaCorrecta;
        }

        public String getPregunta() {
            return pregunta;
        }

        public boolean esCorrecta(String respuesta) {
            return respuesta.trim().equalsIgnoreCase(respuestaCorrecta);
        }
    }

    // Clase para registrar las respuestas en un archivo .txt
    static class RegistroServidor {

        private static int contador = 1;

        public static synchronized void registrarPreguntaYRespuesta(String pregunta, String respuesta, String resultado) {
            LocalDateTime fechaHoraActual = LocalDateTime.now();
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String fechaHora = fechaHoraActual.format(formato);

            String mensaje = String.format("%d - Fecha: %s | Pregunta: %s | Respuesta: %s | Resultado: %s", 
                                           contador++, fechaHora, pregunta, respuesta, resultado);

            try (BufferedWriter escritor = new BufferedWriter(new FileWriter("registro_respuestas.txt", true))) {
                escritor.write(mensaje);
                escritor.newLine();
            } catch (IOException e) {
                System.err.println("Error al escribir en el archivo: " + e.getMessage());
            }
        }
    }
}
