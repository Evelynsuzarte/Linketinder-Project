package view

class Menu {

    private final LeitorConsole leitor

    Menu(LeitorConsole leitor) {
        this.leitor = leitor
    }

    int menu(Scanner scanner){
        println("===== LINKETINDER =====")
        println("1 - LISTAR CANDIDATOS")
        println("2 - LISTAR EMPRESAS")
        println("3 - LISTAR VAGAS")
        println("4 - CADASTRAR CANDIDADOS NOVOS")
        println("5 - CADASTRAR EMPRESAS NOVAS")
        println("6 - CADASTRAR VAGAS NOVAS")
        println("7 - CANDIDATO: CURTIR VAGAS")
        println("8 - EMPRESA: CURTIR CANDIDATOS")
        println("9 - VER MATCHS")
        println("10 - EMPRESA: GERENCIAR RECRUTADORES")
        println("11 - RECRUTADOR: CRIAR/EDITAR VAGAS")
        println("12 - RECRUTADOR: AVALIAR CANDIDATOS")
        println("13 - VER CANDIDATOS REJEITADOS")
        println("0 - SAIR")
        println("===== ESCOLHA:")

        String texto = leitor.lerObrigatorio(scanner, "----->")

        return texto.isInteger() ? texto.toInteger() : -1
    }
}
