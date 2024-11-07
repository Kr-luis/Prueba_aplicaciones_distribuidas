import java.net.*;
import java.util.Scanner;

public class ClienteUDP {
    private static final String HOST = "localhost"; 
    private static final int PUERTO = 3003; 

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress serverAddress = InetAddress.getByName(HOST);
            Scanner scanner = new Scanner(System.in);
            byte[] buffer;

           
            String mensaje = "¿Estás listo para el test?";
            buffer = mensaje.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, PUERTO);
            socket.send(packet); 

         
            for (int i = 0; i < 5; i++) {
             
                buffer = new byte[1024];
                DatagramPacket packetRecibido = new DatagramPacket(buffer, buffer.length);
                socket.receive(packetRecibido);  
                mensaje = new String(packetRecibido.getData(), 0, packetRecibido.getLength());
                System.out.println(mensaje);

                System.out.print("Respuesta: ");
                String respuesta = scanner.nextLine();
                buffer = respuesta.getBytes();
                packet = new DatagramPacket(buffer, buffer.length, serverAddress, PUERTO);
                socket.send(packet);  

        
                buffer = new byte[1024];
                packetRecibido = new DatagramPacket(buffer, buffer.length);
                socket.receive(packetRecibido);  
                mensaje = new String(packetRecibido.getData(), 0, packetRecibido.getLength());
                System.out.println(mensaje);
            }

    
            buffer = new byte[1024];
            DatagramPacket packetRecibido = new DatagramPacket(buffer, buffer.length);
            socket.receive(packetRecibido);  
            mensaje = new String(packetRecibido.getData(), 0, packetRecibido.getLength());
            System.out.println(mensaje);

          
            buffer = new byte[1024];
            packetRecibido = new DatagramPacket(buffer, buffer.length);
            socket.receive(packetRecibido);  
            mensaje = new String(packetRecibido.getData(), 0, packetRecibido.getLength());
            System.out.println(mensaje);

          
            if (mensaje.contains("Gracias por participar")) {
                System.out.println("Conexión terminada.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
