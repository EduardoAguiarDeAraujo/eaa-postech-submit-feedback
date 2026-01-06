package br.eng.eaa.lambda.model;

public class CalculadorUrgencia {

    public static Urgencia calcular(int nota) {
        if (nota <= 5) return Urgencia.ALTA;
        if (nota <= 8) return Urgencia.MEDIA;
        return Urgencia.BAIXA;
    }

}
