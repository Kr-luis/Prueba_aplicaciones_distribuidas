public class Pregunta {
    private String pregunta;
    private String respuestaCorrecta;

    public Pregunta(String pregunta, String respuestaCorrecta) {
        this.pregunta = pregunta;
        this.respuestaCorrecta = normalizarTexto(respuestaCorrecta);
    }

    public String getPregunta() {
        return pregunta;
    }

    public boolean esCorrecta(String respuestaUsuario) {
        return normalizarTexto(respuestaUsuario).equalsIgnoreCase(normalizarTexto(respuestaCorrecta));
    }

    private String normalizarTexto(String texto) {
        return java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "").trim();
    }
}
