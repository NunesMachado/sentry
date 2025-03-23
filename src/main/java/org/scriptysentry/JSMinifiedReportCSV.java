package org.scriptysentry;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JSMinifiedReportCSV {

    private final List<JSFileReport> filesReports = new ArrayList<>();
    private final List<JSFileReport> errorReports = new ArrayList<>();

    public void addToReport(JSFileReport fileReport) {
        if (fileReport.hasError()) {
            errorReports.add(fileReport);
            LogUtil.logWarning("Erro ao analisar arquivo: %s", fileReport.getFile());
        } else {
            filesReports.add(fileReport);
            LogUtil.logInfo("Arquivo adicionado ao relatório: %s", fileReport.getFile());
        }
    }


    public void createCSVReport(String outputPath) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(outputPath))) {
            // Cabeçalhos do CSV
            writer.writeNext(new String[] {"Arquivo", "Minificado", "Biblioteca de Terceiros", "Caminho", "Erro ao ler", "Mensagem de erro"});

            // Adicionando arquivos sem erro ao CSV
            for (JSFileReport report : filesReports) {
                writer.writeNext(new String[] {
                        report.getFile(),
                        String.valueOf(report.isMinified() ? "SIM" : "NÃO"),
                        String.valueOf(report.isLibrary() ? "SIM" : "NÃO"),
                        report.getPath(),
                        String.valueOf(report.hasError() ? "SIM" : "NÃO"),
                        report.hasError() ? report.getException().getCause().getMessage() : ""
                });
            }

            // Adicionando arquivos com erro ao CSV
            for (JSFileReport report : errorReports) {
                writer.writeNext(new String[] {
                        report.getFile(),
                        String.valueOf(report.isMinified() ? "SIM" : "NÃO"),
                        String.valueOf(report.isLibrary() ? "SIM" : "NÃO"),
                        report.getPath(),
                        String.valueOf(report.hasError() ? "SIM" : "NÃO"),
                        report.hasError() ? report.getException().getMessage() : ""
                });
            }

            LogUtil.logInfo("Relatório CSV gerado com sucesso: " + outputPath);
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.logError("Erro ao gerar o arquivo CSV", e);
        }
    }
}
