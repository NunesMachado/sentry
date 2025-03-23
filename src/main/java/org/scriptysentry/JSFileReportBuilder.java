package org.scriptysentry;

import java.nio.file.Path;

public class JSFileReportBuilder {
    private String file;
    private boolean isLibrary;
    private boolean minified;
    private Exception exception;
    private Path path;

    public JSFileReportBuilder setFile(String file) {
        this.file = file;
        return this;
    }

    public JSFileReportBuilder setIsLibrary(boolean isLibrary) {
        this.isLibrary = isLibrary;
        return this;
    }

    public JSFileReportBuilder setMinified(boolean minified) {
        this.minified = minified;
        return this;
    }

    public JSFileReportBuilder setException(Exception exception) {
        this.exception = exception;
        return this;
    }

    public JSFileReportBuilder setPath(Path path) {
        this.path = path;
        return this;
    }

    public JSFileReport createJSFileReport() {
        return new JSFileReport(file, isLibrary, minified, exception, path);
    }

}