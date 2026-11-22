import controller.CandidatoController
import controller.CompetenciaController
import controller.EmpresaController
import controller.LoginController
import controller.MatchController
import controller.RecrutadorController
import controller.VagaController
import org.apache.catalina.Context
import org.apache.catalina.servlets.DefaultServlet
import org.apache.catalina.startup.Tomcat

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class Main {
    static void main(String[] args) {
        Tomcat tomcat = new Tomcat()
        tomcat.setPort(8080)
        tomcat.getConnector()

        String diretorioBase = new File("src/main/resources").getAbsolutePath()
        Context ctx = tomcat.addContext("", diretorioBase)

        ctx.addMimeMapping("html", "text/html")
        ctx.addMimeMapping("css", "text/css")
        ctx.addMimeMapping("js", "application/javascript")
        ctx.addMimeMapping("png", "image/png")
        ctx.addMimeMapping("jpg", "image/jpeg")
        ctx.addMimeMapping("json", "application/json")
        ctx.addMimeMapping("map", "application/json")

        // Serve arquivos estáticos do front-end (/frontend/... e /images/...) e redireciona / para o index
        Tomcat.addServlet(ctx, "default", new DefaultServlet()).addMapping("/")
        Tomcat.addServlet(ctx, "homeRedirectServlet", new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
                resp.sendRedirect("/frontend/html/index.html")
            }
        }).addMapping("")

        // Endpoints da API REST
        Tomcat.addServlet(ctx, "candidatoServlet", new CandidatoController()).addMapping("/candidatos")
        Tomcat.addServlet(ctx, "empresaServlet", new EmpresaController()).addMapping("/empresas")
        Tomcat.addServlet(ctx, "vagaServlet", new VagaController()).addMapping("/vagas")
        Tomcat.addServlet(ctx, "recrutadorServlet", new RecrutadorController()).addMapping("/recrutadores")
        Tomcat.addServlet(ctx, "matchServlet", new MatchController()).addMapping("/matches")
        Tomcat.addServlet(ctx, "loginServlet", new LoginController()).addMapping("/login")
        Tomcat.addServlet(ctx, "competenciaServlet", new CompetenciaController()).addMapping("/competencias")

        tomcat.start()
        println("Servidor Tomcat iniciado em: http://localhost:8080")
        tomcat.getServer().await()
    }
}
