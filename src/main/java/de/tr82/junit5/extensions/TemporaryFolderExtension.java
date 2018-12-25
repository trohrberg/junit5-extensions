package de.tr82.junit5.extensions;

import org.junit.jupiter.api.extension.*;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedList;

import static java.util.Arrays.stream;

public class TemporaryFolderExtension implements AfterEachCallback, TestInstancePostProcessor, ParameterResolver {

    private final Collection<TemporaryFolder> tempFolders;

    public TemporaryFolderExtension() {
        tempFolders = new LinkedList<>();
    }

    @Override
    public void afterEach(ExtensionContext context) {
        tempFolders.forEach(TemporaryFolder::cleanUp);
        tempFolders.clear();
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) {
        stream(testInstance.getClass().getDeclaredFields())
                .filter(field -> field.getType() == TemporaryFolder.class)
                .forEach(field -> injectTemporaryFolder(testInstance, field));
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType() == TemporaryFolder.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return createTempFolder();
    }

    private void injectTemporaryFolder(Object testInstance, Field field) {
        field.setAccessible(true);

        try {
            field.set(testInstance, createTempFolder());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private TemporaryFolder createTempFolder() {
        TemporaryFolder temporaryFolder = new TemporaryFolder();
        temporaryFolder.prepare();
        tempFolders.add(temporaryFolder);

        return temporaryFolder;
    }
}
