package view

class LeitorConsole {

    String lerObrigatorio(Scanner scanner, String mensagem) {
        String texto = ""
        while (texto.isEmpty()) {
            print(mensagem)
            texto = scanner.nextLine().trim()
            if (texto.isEmpty()) {
                println("Campo obrigatório. Tente novamente.")
            }
        }
        return texto
    }

    int lerInteiro(Scanner scanner, String mensagem) {
        while (true) {
            print(mensagem)
            String texto = scanner.nextLine().trim()
            if (texto.isInteger()) {
                return texto.toInteger()
            }
            println("Digite apenas números.")
        }
    }

    List<String> lerCompetencias(Scanner scanner, String mensagem) {
        print(mensagem)
        return scanner.nextLine().split(",").collect { it.trim() }.findAll { !it.isEmpty() }
    }
}
