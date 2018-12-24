package de.tr82.junit5.extensions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Random;

import static java.nio.file.FileVisitResult.CONTINUE;

public class TemporaryFolder {

    private File rootFolder;

    public Path getPath() {
        return rootFolder.toPath();
    }

    public File createFolder(final String name) {
        File file = new File(rootFolder, name);
        file.mkdir();
        return file;
    }

    public File createFile(String name) throws IOException {
        File file = new File(rootFolder, name);
        file.createNewFile();
        return file;
    }

    public File createBinaryFile(final String name, final int length) throws IOException {
        File file = createFile(name);
        writeRandomBytesToFile(length, file);
        return file;
    }

    private void writeRandomBytesToFile(int length, File file) throws IOException {
        final byte[] bytes = new byte[length];
        new Random().nextBytes(bytes);
        try (FileOutputStream os = new FileOutputStream(file)) {
            os.write(bytes);
            os.flush();
        }
    }

    public void prepare() {
        try {
            rootFolder = File.createTempFile("junit5-", ".tmp");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        rootFolder.delete();
        rootFolder.mkdir();
    }

    void cleanUp() {
        try {
            Files.walkFileTree(rootFolder.toPath(), new DeleteAllVisitor());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class DeleteAllVisitor extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
            Files.delete(file);
            return CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path directory, IOException exception) throws IOException {
            Files.delete(directory);
            return CONTINUE;
        }
    }
}
