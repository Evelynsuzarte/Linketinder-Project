
package controller.api

import org.apache.catalina.startup.Tomcat
import org.apache.catalina.Context
import org.apache.catalina.Wrapper

class ApiServer {

    static void iniciar() {

        Tomcat tomcat = new Tomcat()

        tomcat.setPort(8080)

        // Inicializa o conector HTTP
        tomcat.getConnector()

        // Cria o contexto da aplicação
        Context contexto = tomcat.addContext("/linketinder", null)

        // Registra os Servlets
        Wrapper candidato = Tomcat.addServlet(contexto, "CandidatoServlet", new CandidatoServlet()
        )

        candidato.setLoadOnStartup(1)
        contexto.addServletMapping("/candidatos", "CandidatoServlet")

        Wrapper empresa = Tomcat.addServlet(contexto, "EmpresaServlet", new EmpresaServlet())

        empresa.setLoadOnStartup(1)
        contexto.addServletMapping("/empresas", "EmpresaServlet")

        Wrapper vaga = Tomcat.addServlet(contexto, "VagaServlet", new VagaServlet())

        vaga.setLoadOnStartup(1)
        contexto.addServletMapping("/vagas", "VagaServlet")

        // Inicia o servidor
        tomcat.start()

        println("Linketinder API iniciada!")
        println("Servidor: http://localhost:8080/linketinder")

        tomcat.server.await()
    }
}
