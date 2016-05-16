package br.com.esign.postdenuncia.util;

public class ValidationUtil {

    public static void validarEmail(String email) {
        String regex = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        if (!email.matches(regex)) {
            throw new IllegalArgumentException(MessagesBundle.ERRO_EMAIL_INVALIDO);
        }
    }

    public static void validarCelular(String celular) {
        if (celular.length() < 10 || (celular.length() == 11 && !celular.substring(2, 3).equals("9"))) {
            throw new IllegalArgumentException(MessagesBundle.ERRO_CELULAR_INVALIDO);
        }
    }

}