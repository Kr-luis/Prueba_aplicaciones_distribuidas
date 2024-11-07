import java.net.*;
import java.util.Collections;
import java.util.List;

class HiloClienteUDP extends Thread {
    private DatagramSocket socket;
    private List<Pregunta> preguntas;
    private InetAddress clienteAddress;
    private int clientePort;

    public HiloClienteUDP(DatagramSocket socket, List<Pregunta> preguntas, InetAddress clienteAddress, int clientePort) {
        this.socket = socket;
        this.preguntas = preguntas;
        this.clienteAddress = clienteAddress;
        this.clientePort = clientePort;
    }

    private String normalizarTexto(String texto) {
        return java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    @Override
    public void run() {
        try {
            boolean repetirTest = true;
            while (repetirTest) {
                // Seleccionar solo 5 preguntas aleatorias
                Collections.shuffle(preguntas);
                List<Pregunta> preguntasSeleccionadas = preguntas.subList(0, 5);
                int puntaje = 0;

                // Hacer las 5 preguntas
                for (Pregunta pregunta : preguntasSeleccionadas) {
                    // Enviar la pregunta al cliente
                    String mensaje = "Pregunta: " + pregunta.getPregunta();
                    byte[] buffer = mensaje.getBytes();
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, clienteAddress, clientePort);
                    socket.send(packet); // Enviar pregunta

                    // Recibir respuesta del cliente
                    buffer = new byte[1024];
                    DatagramPacket respuestaPacket = new DatagramPacket(buffer, buffer.length);
                    socket.receive(respuestaPacket); // Recibir respuesta del cliente
                    String respuesta = new String(respuestaPacket.getData(), 0, respuestaPacket.getLength());

                    String resultado;
                    if (pregunta.esCorrecta(normalizarTexto(respuesta))) {
                        puntaje += 4;  // Si la respuesta es correcta
                        resultado = "Correcto";
                    } else {
                        resultado = "Incorrecto";
                    }

                    // Enviar el resultado (Correcto o Incorrecto) al cliente
                    mensaje = resultado;
                    buffer = mensaje.getBytes();
                    packet = new DatagramPacket(buffer, buffer.length, clienteAddress, clientePort);
                    socket.send(packet); // Enviar resultado al cliente
                }

                // Enviar el puntaje final
                String mensajeFinal = "Puntaje final: " + puntaje + "/20"; // 5 preguntas * 4 puntos cada una
                byte[] buffer = mensajeFinal.getBytes();
                DatagramPacket packetFinal = new DatagramPacket(buffer, buffer.length, clienteAddress, clientePort);
                socket.send(packetFinal);

                // Preguntar si quiere repetir el test
                String mensajeRepetir = "¿Quieres hacer el test nuevamente? (si/no)";
                buffer = mensajeRepetir.getBytes();
                DatagramPacket packetRepetir = new DatagramPacket(buffer, buffer.length, clienteAddress, clientePort);
                socket.send(packetRepetir);

                buffer = new byte[1024];
                DatagramPacket respuestaPacketRepetir = new DatagramPacket(buffer, buffer.length);
                socket.receive(respuestaPacketRepetir); // Recibir respuesta si desea repetir el test
                String respuestaRepetir = new String(respuestaPacketRepetir.getData(), 0, respuestaPacketRepetir.getLength());

                if (respuestaRepetir.equalsIgnoreCase("si")) {
                    repetirTest = true;  // Repetir el test
                } else {
                    repetirTest = false;  // Terminar el test
                    String despedida = "Gracias por participar. Conexión terminada.";
                    buffer = despedida.getBytes();
                    DatagramPacket packetDespedida = new DatagramPacket(buffer, buffer.length, clienteAddress, clientePort);
                    socket.send(packetDespedida); // Enviar despedida
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
