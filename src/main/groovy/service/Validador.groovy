package service

import model.repository.CandidatoRepository
import model.repository.CompetenciaRepository
import model.repository.EmpresaRepository
import model.repository.MatchRepository
import model.repository.VagaRepository

import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.ResolverStyle
import java.util.regex.Pattern

class Validador {
    private static final Pattern PADRAO_EMAIL = Pattern.compile(/^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/)
    private static final DateTimeFormatter FORMATO_DATA_BR = DateTimeFormatter
            .ofPattern("dd-MM-uuuu")
            .withResolverStyle(ResolverStyle.STRICT)

    private final CandidatoRepository repoCandidato
    private final CompetenciaRepository repoCompetencia
    private final EmpresaRepository repoEmpresa
    private final MatchRepository repoMatch
    private final VagaRepository repoVaga

    static boolean emailValido(String email) {
        if (email == null) {
            return false
        }
        return PADRAO_EMAIL.matcher(email.trim()).matches()
    }

    static String limparCpf(String cpf) {
        return cpf == null ? "" : cpf.replaceAll(/\D/, "")
    }

    static boolean cpfValido(String cpf) {
        String digitos = limparCpf(cpf)
        if (digitos.length() != 11) {
            return false
        }
        if (digitos.chars().distinct().count() == 1) {
            return false
        }
        int primeiroDigito = calcularDigitoCpf(digitos, 9)
        int segundoDigito = calcularDigitoCpf(digitos, 10)
        return digitos[9].toInteger() == primeiroDigito && digitos[10].toInteger() == segundoDigito
    }

    private static int calcularDigitoCpf(String digitos, int tamanhoBase) {
        int soma = 0
        int pesoInicial = tamanhoBase + 1
        for (int i = 0; i < tamanhoBase; i++) {
            soma += digitos[i].toInteger() * (pesoInicial - i)
        }
        int resto = (soma * 10) % 11
        return resto == 10 ? 0 : resto
    }

    static LocalDate parseDataNascimento(String dataTexto) {
        if (dataTexto == null || dataTexto.trim().isEmpty()) {
            throw new IllegalArgumentException("Data de nascimento é obrigatória no formato dd-mm-aaaa.")
        }
        try {
            return LocalDate.parse(dataTexto.trim(), FORMATO_DATA_BR)
        } catch (DateTimeParseException ignored) {
            throw new IllegalArgumentException("Data de nascimento inválida. Use o formato dd-mm-aaaa.")
        }
    }

    static boolean maiorDeIdade(LocalDate dataNascimento, LocalDate hoje = LocalDate.now()) {
        if (dataNascimento == null || dataNascimento.isAfter(hoje)) {
            return false
        }
        return Period.between(dataNascimento, hoje).years >= 18
    }
}
