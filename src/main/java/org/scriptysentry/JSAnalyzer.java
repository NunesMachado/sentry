package org.scriptysentry;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

class JSAnalyzer {

//    public static final int MIN_MINIFIED_LINES = 10;
    public static final int NO_LINES = 0;
    public static final int DEFAULT_AVERAGE = 200;


    public static Optional<JSFileReport> analyzeJSFile(Path path) {
        try {
            LogUtil.logInfo("Analisando arquivo: " + path);
            return analyze(path);
        } catch (IOException e) {
            LogUtil.logError("Erro ao analisar arquivo: " + path, e);
            return fileReportWithError(path, e);
        }
    }

    private static Optional<JSFileReport> fileReportWithError(Path path, IOException e) {
        return Optional.of(new JSFileReportBuilder()
                .setFile(getFileName(path))
                .setException(e)
                .setPath(path)
                .createJSFileReport());
    }

    private static Optional<JSFileReport> analyze(Path path) throws IOException {
        final String fileName = getFileName(path);
        final boolean isMinified = checkIfIsMinified(path);
        final boolean isLibrary = checkIfIsALibrary(path);

        LogUtil.logInfo("Arquivo: " + fileName + " | Minificado: " + isMinified + " | Biblioteca: " + isLibrary);

        return Optional.of(new JSFileReportBuilder()
                .setFile(fileName)
                .setIsLibrary(isLibrary)
                .setMinified(isMinified)
                .setPath(path)
                .setException(null)
                .createJSFileReport());
    }

    private static String getFileName(Path path) {
        return path.getFileName().toString().toLowerCase();
    }


    private static boolean checkIfIsALibrary(Path path) {
        return Arrays.stream(Libraries.values())
                .anyMatch(lib -> getFileName(path).contains(lib.getName()) || getFileAbsolutePathName(path).contains(lib.getName()));
    }

    private static String getFileAbsolutePathName(Path path) {
        return path.toAbsolutePath().toString();
    }

    private static boolean checkIfIsMinified(Path path) throws IOException {
        Charset charset = detectCharset(path);
        final List<String> lines = Files.readAllLines(path, charset);
        return /*lines.size() < MIN_MINIFIED_LINES
                ||*/ lines.stream().mapToInt(String::length).average().orElse(NO_LINES) > DEFAULT_AVERAGE;
    }

    private static Charset detectCharset(Path path) throws IOException {


        byte[] buf = new byte[4096];
        try (InputStream inputStream = Files.newInputStream(path)) {
            UniversalDetector detector = new UniversalDetector(null);
            int nread;
            while ((nread = inputStream.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }
            detector.dataEnd();
            String encoding = detector.getDetectedCharset();
            detector.reset();
            return encoding != null ? Charset.forName(encoding) : StandardCharsets.UTF_8;
        }
    }
}