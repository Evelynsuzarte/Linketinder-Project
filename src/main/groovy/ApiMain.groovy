class ApiMain {
    static void main(String[] args) {

        // 1. Instanciar os repositórios
        // 2. Instanciar os serviços com os repositórios
        // 3. Iniciar o servidor

        ApiServer.iniciar(
                candidatoService,
                empresaService,
                vagaService
        )
    }
}
