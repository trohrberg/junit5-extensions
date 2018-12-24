package de.tr82.junit5.extensions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(TemporaryFolderExtension.class)
public class TemporaryFolderExtensionTest {

    private TemporaryFolder tempFolder;

    @Test
    public void testFieldInjection() {
        assertNotNull(tempFolder);
    }

    @Test
    public void testParamInjection(final TemporaryFolder folder) {
        assertNotNull(folder);
    }

    @Test
    public void testCreateFile(final TemporaryFolder tmpFolder) throws IOException {
        File file = tmpFolder.createFile("testfile.bin");
        assertTrue(file.exists());
        assertTrue(file.isFile());
        assertEquals(0, file.length());
    }

    @Test
    public void testCreateBinaryFile(final TemporaryFolder folder) throws IOException {
        final File file = folder.createBinaryFile("testfile.bin", 1024);
        assertTrue(file.exists());
        assertTrue(file.isFile());
        assertEquals(1024, file.length());
    }

    @Test
    public void testCreateSubFolder(final TemporaryFolder folder) {
        File subfolder = folder.createFolder("subfolder");
        assertTrue(subfolder.exists());
        assertTrue(subfolder.isDirectory());
        assertEquals(folder.getPath().toString() + "/subfolder", subfolder.getPath().toString());
    }

    @Test
    public void testCreateFileInSubFolder(final TemporaryFolder folder) throws IOException {
        File subfolder = folder.createFolder("subfolder");
        File testfile = new File(subfolder, "testfile.bin");
        testfile.createNewFile();

        assertTrue(testfile.exists());
        assertTrue(testfile.isFile());
        assertEquals(folder.getPath().toString() + "/subfolder/testfile.bin", testfile.getPath().toString());
    }
}
