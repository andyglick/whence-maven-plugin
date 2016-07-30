import aQute.bnd.osgi.Builder;
import aQute.bnd.osgi.ClassDataCollector;
import aQute.bnd.osgi.Clazz;
import aQute.bnd.osgi.Descriptors;
import aQute.bnd.osgi.FileResource;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Resource;
import aQute.bnd.osgi.ZipResource;
import com.google.common.base.Preconditions;
import org.joor.Reflect;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.zip.ZipFile;

import static java.lang.String.format;

public class TestBnd {

    //@Test
    public void bnd_builder() throws Exception {

        Jar[] classPath = m2Jars(
                "com.querydsl:querydsl-core:jar:4.0.7",
                "com.google.guava:guava:jar:18.0",
                "com.mysema.commons:mysema-commons-lang:jar:0.2.4"
        );

        Jar jar = m2Jars("com.querydsl:querydsl-sql:jar:4.0.7")[0];

        Builder bndBuilder = new Builder();
        bndBuilder.setJar(jar);
        bndBuilder.setClasspath(classPath);
        bndBuilder.analyze();

        Map<Descriptors.TypeRef, Clazz> classspace = bndBuilder.getClassspace();
        Collection<Descriptors.TypeRef> typeRefs = sort(classspace.keySet());
        for (Descriptors.TypeRef typeRef : typeRefs) {
            Clazz clazz = classspace.get(typeRef);
            Optional<File> classFile = findClassFile(clazz);
            System.out.println(String.format(" class : %s", typeRef.getFQN()));
            System.out.println(String.format("    path : %s", classFile.map(File::getAbsolutePath).orElse("unknown")));
            //printClass(clazz);
        }


    }

    private Collection<Descriptors.TypeRef> sort(Set<Descriptors.TypeRef> typeRefs) {
        List<Descriptors.TypeRef> list = new ArrayList<>(typeRefs);
        Collections.sort(list, (o1, o2) -> o2.getFQN().compareTo(o2.getFQN()));
        return list;
    }

    private Optional<File> findClassFile(Clazz clazz) {
        //
        // we wished that there was a back link from clazz to the file/zip that contains that class
        // but there is not and we don't know what JAR is being used during BND analysis
        // so we use this technique
        //
        Resource resource = Reflect.on(clazz).get("resource");
        if (resource instanceof ZipResource) {
            ZipFile zipFile = Reflect.on(resource).get("zip");
            return Optional.of(new File(zipFile.getName()));
        }
        if (resource instanceof FileResource) {
            File f = Reflect.on(resource).get("file");
            return Optional.of(f);
        }
        return Optional.empty();
    }

    private Jar[] m2Jars(String... gavs) throws IOException {
        File home = new File(System.getProperty("user.home"));
        File m2Repo = new File(home, ".m2/repository");

        Jar[] jars = new Jar[gavs.length];
        int i = 0;
        for (String gav : gavs) {
            String[] split = gav.split(":");
            Preconditions.checkArgument(split.length == 4, "bad gav : " + gav);

            String groupId = split[0].replace(".", "/");
            String artifactId = split[1];
            String type = split[2];
            String version = split[3];

            String path = "/" + groupId + "/" + artifactId + "/" + version + "/" + artifactId + "-" + version + "." + type;
            File jarFile = new File(m2Repo, path);
            Jar jar = new Jar(jarFile);
            jars[i] = jar;
            i++;
        }
        return jars;
    }

    private void printClass(Clazz clazz) {
        Set<Descriptors.PackageRef> referred = clazz.getReferred();
        System.out.println(format("\t refers to (%d)", referred.size()));
        for (Descriptors.PackageRef ref : referred) {
            System.out.println(format("\t\t %s", ref.getFQN()));
            inspect(clazz);
        }
    }

    private void inspect(final Clazz clazz) {
        try {
            clazz.parseClassFileWithCollector(new ClassDataCollector() {
                @Override
                public void referTo(Descriptors.TypeRef typeRef, int modifiers) {
                    System.out.println(format("\t\t\t %s refers : %s", clazz.getFQN(), typeRef.getFQN()));
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
