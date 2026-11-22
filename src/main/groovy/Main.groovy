import controller.CandidatoController
import controller.EmpresaController
import controller.LoginController
import controller.MatchController
import controller.RecrutadorController
import controller.VagaController
import org.apache.catalina.Context
import org.apache.catalina.startup.Tomcat

class Main {
    static void main(String[] args) {
        Tomcat tomcat = new Tomcat()
        tomcat.setPort(8080)
        tomcat.getConnector()

        String diretorioBase = new File(".").getAbsolutePath()
        Context ctx = tomcat.addContext("", diretorioBase)

        // Forma atualizada sem métodos deprecated:
        Tomcat.addServlet(ctx, "candidatoServlet", new CandidatoController()).addMapping("/candidatos")
        Tomcat.addServlet(ctx, "empresaServlet", new EmpresaController()).addMapping("/empresas")
        Tomcat.addServlet(ctx, "vagaServlet", new VagaController()).addMapping("/vagas")
        Tomcat.addServlet(ctx, "recrutadorServlet", new RecrutadorController()).addMapping("/recrutadores")
        Tomcat.addServlet(ctx, "matchServlet", new MatchController()).addMapping("/matches")
        Tomcat.addServlet(ctx, "loginServlet", new LoginController()).addMapping("/login")

        tomcat.start()
        println("Servidor Tomcat iniciado em: http://localhost:8080")
        tomcat.getServer().await()
    }
}