package org.scriptysentry;

import java.nio.file.Path;

public class JSFileReport {
    private final String file;
    private final boolean minified;
    private final boolean isLibrary;
    private final Exception exception;
    private final Path path;

    public JSFileReport(String file, boolean isLibrary, boolean minified, Exception exception, Path path) {
        this.file = file;
        this.minified = minified;
        this.isLibrary = isLibrary;
        this.exception = exception;
        this.path = path;
    }

    public Exception getException() {
        return exception;
    }

    public String getFile() {
        return file;
    }

    public boolean isMinified() {
        return minified;
    }

    public boolean isLibrary() {
        return isLibrary;
    }

    public String getPath() {
        return this.path.toAbsolutePath().toString();
    }

    public boolean hasError() {
        return exception != null;
    }
}
