package org.scriptysentry;

public enum Category {
    MINIFIED_LIBRARY("Arquivos Minificados - Bibliotecas", true, true),
    MINIFIED_BUSINESS("Arquivos Minificados - Regras de Negócio", true, false),
    UNMINIFIED_LIBRARY("Arquivos Não Minificados - Bibliotecas", false, true),
    UNMINIFIED_BUSINESS("Arquivos Não Minificados - Regras de Negócio", false, false);

    private final String title;
    private final boolean minified;
    private final boolean library;

    Category(String title, boolean minified, boolean library) {
        this.title = title;
        this.minified = minified;
        this.library = library;
    }

    public String getTitle() {
        return title;
    }

    public boolean matches(JSFileReport report) {
        return report.isMinified() == minified && report.isLibrary() == library;
    }
}