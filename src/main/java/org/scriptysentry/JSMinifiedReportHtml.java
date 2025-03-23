package org.scriptysentry;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class JSMinifiedReportHtml {
    private static final Logger LOGGER = Logger.getLogger(JSMinifiedReportHtml.class.getName());
    private final List<JSFileReport> filesReports = new ArrayList<>();
    private final List<JSFileReport> errorReports = new ArrayList<>();

    public void addToReport(JSFileReport jsFileReport) {
        if (jsFileReport.hasError()) {
            errorReports.add(jsFileReport);
            LogUtil.logWarning("Erro ao analisar arquivo: %s", jsFileReport.getFile());
        } else {
            filesReports.add(jsFileReport);
            LogUtil.logInfo("Arquivo adicionado ao relatório: %s", jsFileReport.getFile());
        }
    }

    public void createHtmlReport(String outputPath) {
        LogUtil.logInfo("Iniciando geração do relatório HTML...");

        StringBuilder html = new StringBuilder()
                .append(getHtmlHeader())
                .append("<h1>Relatório de Arquivos JS</h1>");

        appendPieChartScript(html);
        appendSummary(html);
        generateCategorySections(html);
        appendErrorTable(html);

        html.append("</body></html>");
        writeToFile(outputPath, html.toString());
    }

    private void appendSummary(StringBuilder html) {
        html.append("<h2>Resumo</h2>").append("<ul>");

        Arrays.stream(Category.values())
                .forEach(category -> {
                    long count = filesReports.stream().filter(category::matches).count();
                    html.append(String.format("<li><strong>%s:</strong> %d arquivos</li>", category.getTitle(), count));
                });

        html.append("</ul>");
    }

    private void generateCategorySections(StringBuilder html) {
        Arrays.stream(Category.values()).forEach(category -> appendCategory(html, category));
    }

    private void appendCategory(StringBuilder html, Category category) {
        List<JSFileReport> filteredReports = filesReports.stream()
                .filter(category::matches)
                .collect(Collectors.toList());

        if (filteredReports.isEmpty()) return;

        long categoryCount = filteredReports.size();
        Map<String, Long> folderCounts = filteredReports.stream()
                .collect(Collectors.groupingBy(this::getParentFolder, Collectors.counting()));

        String categoryId = category.name().toLowerCase().replace("_", "-");

        html.append(String.format("<details class='category' id='%s'>", categoryId))
                .append(String.format("<summary>%s (%d arquivos, %d pastas)</summary>", category.getTitle(), categoryCount, folderCounts.size()));

        filteredReports.stream()
                .collect(Collectors.groupingBy(this::getCategory))
                .forEach((categoryName, reports) -> {
                    html.append("<div class='indent-1'>");
                    html.append(String.format("<details class='subgroup'><summary>%s (%d arquivos)</summary>", categoryName, reports.size()));
                    appendGroupedByParent(html, reports, 2);
                    html.append("</details>");
                    html.append("</div>");
                });
        html.append("</details>");
    }

    private void appendGroupedByParent(StringBuilder html, List<JSFileReport> reports, int level) {
        reports.stream()
                .collect(Collectors.groupingBy(this::getParentFolder))
                .forEach((parent, subReports) -> {
                    String folderId = parent.toLowerCase().replace("\\", "-").replace("/", "-");
                    html.append(String.format("<div class='indent-%d'>", level));
                    html.append(String.format("<details class='subgroup' id='%s'><summary>%s (%d arquivos)</summary>", folderId, parent, subReports.size()));
                    html.append("<table><tr><th>Arquivo</th><th>Caminho</th></tr>");
                    subReports.forEach(report -> html.append(String.format(
                            "<tr class='file'><td>%s</td><td>%s</td></tr>",
                            report.getFile(), report.getPath()
                    )));
                    html.append("</table>");
                    html.append("</details>");
                    html.append("</div>");
                });
    }

    private void appendErrorTable(StringBuilder html) {
        if (errorReports.isEmpty()) return;

        html.append("<details class='category' id='errors'>")
                .append("<summary>Arquivos com Erros de Leitura (").append(errorReports.size()).append(")</summary>")
                .append("<table>")
                .append("<tr><th>Arquivo</th><th>Caminho Completo</th><th>Erro</th></tr>");

        errorReports.forEach(report -> html.append("<tr><td>")
                .append(report.getFile())
                .append("</td><td>")
                .append(report.getPath())
                .append("</td><td>")
                .append(report.getException().getMessage())
                .append("</td></tr>"));

        html.append("</table></details>");
    }

    private void writeToFile(String outputPath, String content) {
        try (FileWriter writer = new FileWriter(outputPath)) {
            writer.write(content);
            LogUtil.logInfo("Relatório HTML gerado com sucesso: %s", outputPath);
        } catch (IOException e) {
            LogUtil.logError("Erro ao gerar relatório: %s", e);
        }
    }


    private String getHtmlHeader() {
        return "<html><head><title>JS Minified Report</title>"
                + "<style>"
                + "body { font-family: Arial, sans-serif; margin: 20px; }"
                + "table { width: 100%; border-collapse: collapse; margin-top: 10px; }"
                + "th, td { border: 1px solid black; padding: 8px; text-align: left; }"
                + "th { background-color: #f2f2f2; }"
                + "details { margin: 5px 0; }"
                + "summary { cursor: pointer; font-size: 16px; padding: 5px; background: #f8f8f8; border: 1px solid #ccc; border-radius: 5px; }"
                + ".indent-1 { padding-left: 20px; }"
                + ".indent-2 { padding-left: 40px; }"
                + ".indent-3 { padding-left: 60px; }"
                + ".category { font-weight: bold; }"
                + ".subgroup { font-style: italic; }"
                +"th:nth-child(2), td:nth-child(2) {"
                +"word-break: break-all; "
                +"max-width: 600px; "
                +"}"
                + "</style>"
                + "<script>"
                + "function toggleAll(expand) {"
                + "  document.querySelectorAll('details').forEach(d => expand ? d.setAttribute('open', '') : d.removeAttribute('open'));"
                + "}"
                + "</script>"
                + "</head><body>"
                + "<button onclick='toggleAll(true)'>Expandir Tudo</button>"
                + "<button onclick='toggleAll(false)'>Fechar Tudo</button>";
    }

    private String getCategory(JSFileReport report) {
        return Arrays.stream(Modules.values())
                .filter(module -> report.getPath().toLowerCase().contains("\\" + module.getModule() + "\\"))
                .findFirst()
                .map(Modules::getModule)
                .orElse(Modules.CLIENTE.getModule());
    }

    private void appendPieChartScript(StringBuilder html) {
        long minifiedCount = filesReports.stream().filter(JSFileReport::isMinified).count();
        long notMinifiedCount = filesReports.size() - minifiedCount;

        // Contagem por categoria
        Map<String, Long> categoryCounts = Arrays.stream(Category.values())
                .collect(Collectors.toMap(Category::getTitle,
                        cat -> filesReports.stream().filter(cat::matches).count()));

        html.append("<div style='display: flex; gap: 20px;'>")
                .append("<div id='chart_div' style='width: 500px; height: 400px;'></div>")
                .append("<div id='category_chart_div' style='width: 500px; height: 400px;'></div>")
                .append("</div>");

        html.append("<script type='text/javascript' src='https://www.gstatic.com/charts/loader.js'></script>")
                .append("<script>")
                .append("google.charts.load('current', {'packages':['corechart']});")
                .append("google.charts.setOnLoadCallback(drawCharts);")
                .append("function drawCharts() {")

                // Primeiro gráfico (Minificados vs Não Minificados)
                .append("var data1 = google.visualization.arrayToDataTable([")
                .append("['Tipo', 'Quantidade'],")
                .append("['Minificados', ").append(minifiedCount).append("],")
                .append("['Não Minificados', ").append(notMinifiedCount).append("]")
                .append("]);")
                .append("var options1 = {'title': 'Arquivos Minificados', 'pieHole': 0.4};")
                .append("var chart1 = new google.visualization.PieChart(document.getElementById('chart_div'));")
                .append("chart1.draw(data1, options1);")

                // Segundo gráfico (Distribuição por Categoria)
                .append("var data2 = google.visualization.arrayToDataTable([")
                .append("['Categoria', 'Quantidade'],");

        categoryCounts.forEach((category, count) ->
                html.append("['").append(category).append("', ").append(count).append("],"));

        html.append("]);")
                .append("var options2 = {'title': 'Distribuição por Categoria'};")
                .append("var chart2 = new google.visualization.PieChart(document.getElementById('category_chart_div'));")
                .append("chart2.draw(data2, options2);")

                .append("}")
                .append("</script>");
    }



    private String getParentFolder(JSFileReport report) {
        Path parent = Paths.get(report.getPath()).getParent();
        return (parent != null) ? parent.toAbsolutePath().toString() : "Raiz";
    }
}
