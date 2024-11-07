import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RegistroServidor {

    private static int contador = 1;

    public static synchronized void registrarRespuesta(String ipOrigen, String respuesta) {
        LocalDateTime fechaHoraActual = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String fechaHora = fechaHoraActual.format(formato);

        String mensaje = String.format("%d - Fecha: %s | IP: %s | Respuesta: %s", 
                                       contador++, fechaHora, ipOrigen, respuesta);

        try (BufferedWriter escritor = new BufferedWriter(new FileWriter("registro_respuestas.txt", true))) {
            escritor.write(mensaje);
            escritor.newLine();
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo: " + e.getMessage());
        }
    }
}
