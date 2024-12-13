import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class DependencyResolver {
    public static Set<String> retrieveDependenciesFromPomXml(String pomXmlPath) {
        Set<String> dependencies = new HashSet<>();

        try {
            File pomXmlFile = new File(pomXmlPath);
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(new FileInputStream(pomXmlFile));

            // Extract dependencies from the pom.xml file
            model.getDependencies().forEach(dependency -> {
                // Add dependency in the format groupId:artifactId:version to the set
                String dependencyString = String.format("%s:%s:%s",
                        dependency.getGroupId(),
                        dependency.getArtifactId(),
                        dependency.getVersion());
                dependencies.add(dependencyString);
            });
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }

        return dependencies;
    }
}
