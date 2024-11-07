import java.net.*;
import java.util.Scanner;

public class ClienteUDP {
    private static final String HOST = "localhost";  // Dirección del servidor (localhost para el mismo computador)
    private static final int PUERTO = 3003;  // Puerto donde el servidor está escuchando

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress serverAddress = InetAddress.getByName(HOST);
            Scanner scanner = new Scanner(System.in);
            byte[] buffer;

            // Enviar mensaje inicial al servidor
            String mensaje = "¿Estás listo para el test?";
            buffer = mensaje.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, PUERTO);
            socket.send(packet);  // Enviar mensaje

            // Recibir preguntas y responder
            for (int i = 0; i < 5; i++) {
                // Recibir pregunta del servidor
                buffer = new byte[1024];
                DatagramPacket packetRecibido = new DatagramPacket(buffer, buffer.length);
                socket.receive(packetRecibido);  // Recibe la pregunta
                mensaje = new String(packetRecibido.getData(), 0, packetRecibido.getLength());
                System.out.println(mensaje);

                // Responder a las preguntas
                System.out.print("Respuesta: ");
                String respuesta = scanner.nextLine();
                buffer = respuesta.getBytes();
                packet = new DatagramPacket(buffer, buffer.length, serverAddress, PUERTO);
                socket.send(packet);  // Enviar respuesta

                // Recibir resultado de la respuesta
                buffer = new byte[1024];
                packetRecibido = new DatagramPacket(buffer, buffer.length);
                socket.receive(packetRecibido);  // Recibe el resultado (Correcto/Incorrecto)
                mensaje = new String(packetRecibido.getData(), 0, packetRecibido.getLength());
                System.out.println(mensaje);
            }

            // Recibir puntaje final
            buffer = new byte[1024];
            DatagramPacket packetRecibido = new DatagramPacket(buffer, buffer.length);
            socket.receive(packetRecibido);  // Recibe el puntaje final
            mensaje = new String(packetRecibido.getData(), 0, packetRecibido.getLength());
            System.out.println(mensaje);

            // Recibir mensaje de despedida
            buffer = new byte[1024];
            packetRecibido = new DatagramPacket(buffer, buffer.length);
            socket.receive(packetRecibido);  // Recibe el mensaje final de despedida
            mensaje = new String(packetRecibido.getData(), 0, packetRecibido.getLength());
            System.out.println(mensaje);

            // Salir si el mensaje es de despedida
            if (mensaje.contains("Gracias por participar")) {
                System.out.println("Conexión terminada.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
