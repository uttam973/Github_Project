
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class GitHubCloner {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter GitHub repository URL: ");
        String repoUrl = scanner.nextLine();

        // Clone the repository
        cloneRepository(repoUrl);

        // Explore the repository
        RepositoryExplorer.exploreRepository("repository");
    }

    private static void cloneRepository(String repoUrl) {
    try {
        // Use the current working directory as the destination
        Path currentWorkingDir = Paths.get(System.getProperty("user.dir"));
        Path destination = currentWorkingDir.resolve("repository");

        org.eclipse.jgit.api.Git.cloneRepository()
                .setURI(repoUrl)
                .setDirectory(destination.toFile())
                .call();

        System.out.println("Repository cloned successfully!");
    } catch (org.eclipse.jgit.api.errors.TransportException e) {
        System.err.println("Error cloning repository. Check the repository URL and try again.");
    } catch (org.eclipse.jgit.api.errors.GitAPIException e) {
        System.err.println("Error cloning repository: " + e.getMessage());
    }
}


}

