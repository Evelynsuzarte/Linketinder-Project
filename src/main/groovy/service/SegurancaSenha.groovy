package service

import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

class SegurancaSenha {

    private static final String ALGORITMO = "PBKDF2WithHmacSHA256"
    private static final int TAMANHO_SALT_BYTES = 16
    private static final int ITERACOES = 65536
    private static final int TAMANHO_CHAVE_BITS = 256
    private static final SecureRandom RANDOM = new SecureRandom()

    static String gerarHash(String senhaPura) {
        if (!senhaPura?.trim()) {
            throw new IllegalArgumentException("A senha não pode ser vazia.")
        }
        byte[] salt = new byte[TAMANHO_SALT_BYTES]
        RANDOM.nextBytes(salt)
        byte[] hash = calcularPbkdf2(senhaPura.toCharArray(), salt)
        return "${Base64.encoder.encodeToString(salt)}:${Base64.encoder.encodeToString(hash)}"
    }

    static boolean verificarSenha(String senhaDigitada, String hashArmazenado) {
        if (!senhaDigitada || !hashArmazenado || !hashArmazenado.contains(":")) {
            return false
        }
        String[] partes = hashArmazenado.split(":", 2)
        try {
            byte[] salt = Base64.decoder.decode(partes[0])
            byte[] hashEsperado = Base64.decoder.decode(partes[1])
            byte[] hashCalculado = calcularPbkdf2(senhaDigitada.toCharArray(), salt)
            return MessageDigest.isEqual(hashEsperado, hashCalculado)
        } catch (IllegalArgumentException ignored) {
            return false
        }
    }

    static boolean estaHasheada(String valor) {
        if (!valor || !valor.contains(":")) {
            return false
        }
        String[] partes = valor.split(":", 2)
        try {
            byte[] salt = Base64.decoder.decode(partes[0])
            byte[] hash = Base64.decoder.decode(partes[1])
            return salt.length == TAMANHO_SALT_BYTES && hash.length == (TAMANHO_CHAVE_BITS / 8)
        } catch (IllegalArgumentException ignored) {
            return false
        }
    }

    private static byte[] calcularPbkdf2(char[] senha, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(senha, salt, ITERACOES, TAMANHO_CHAVE_BITS)
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITMO)
        return factory.generateSecret(spec).encoded
    }
}
