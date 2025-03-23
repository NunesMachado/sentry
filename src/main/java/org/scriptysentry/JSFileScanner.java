package org.scriptysentry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class JSFileScanner {


    public static final String JS = ".js";
    public static final String NOME_RELATORIO_JS_HTML = "\\relatorioArquivosJs.html";
    public static final String NOME_RELATORIO_JS_CSV = "\\relatorioArquivosJs.csv";


    public static void main(String[] args) throws IOException {

        if (args.length < 1) {
            System.out.println("Uso: java JSFileScanner <diretorio_do_projeto>");
            return;
        }

        JSMinifiedReportHtml reportHTML = new JSMinifiedReportHtml();
        JSMinifiedReportCSV reportCSV = new JSMinifiedReportCSV();

        Path rootPath = Paths.get(args[0]);

        LogUtil.logInfo("Iniciando a varredura no diretório: " + rootPath);

        Files.walk(rootPath)
                .filter( path -> path.toString().endsWith(JS))
                .forEach(path -> {
                    LogUtil.logInfo("Analisando arquivo: " + path);
                    JSAnalyzer.analyzeJSFile(path)
                            .ifPresent(fileReport -> {
                                reportHTML.addToReport(fileReport);
                                reportCSV.addToReport(fileReport);
                            });
                });

        reportHTML.createHtmlReport(rootPath.toAbsolutePath() + NOME_RELATORIO_JS_HTML);
        reportCSV.createCSVReport(rootPath.toAbsolutePath() + NOME_RELATORIO_JS_CSV);
    }

}
