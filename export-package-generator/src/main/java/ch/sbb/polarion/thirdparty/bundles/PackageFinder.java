package ch.sbb.polarion.thirdparty.bundles;

import com.google.common.reflect.ClassPath;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class PackageFinder {

    private static final String PACKAGES_DELIMITER = "," + System.lineSeparator() + " ";
    private static final String MANIFEST_MF = "target/classes/META-INF/MANIFEST.MF";

    public static void main(String[] args) throws IOException {
        Set<String> allExportedPackages = findAllExportedPackages(args);

        String exportPackagesContent = "Export-Package: " + String.join(PACKAGES_DELIMITER, allExportedPackages) + System.lineSeparator();

        appendToManifest(exportPackagesContent);
    }

    public static Set<String> findAllExportedPackages(String[] rootPackages) throws IOException {
        Set<String> allExportedPackages = new HashSet<>();

        for (String rootPackage : rootPackages) {
            Set<String> subPackages = findSubPackages(rootPackage);
            allExportedPackages.addAll(subPackages);
        }

        return allExportedPackages;
    }

    public static Set<String> findSubPackages(String packageName) throws IOException {
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        return ClassPath.from(systemClassLoader)
                .getTopLevelClassesRecursive(packageName)
                .stream()
                .map(ClassPath.ClassInfo::getPackageName)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    public static void appendToManifest(String exportPackagesContent) throws IOException {
        Path path = Paths.get(MANIFEST_MF);
        Files.write(path, exportPackagesContent.getBytes(), StandardOpenOption.APPEND);

        System.out.printf("'Export-Package' entry has been added to file: %s%n", path.toAbsolutePath());
    }

}
