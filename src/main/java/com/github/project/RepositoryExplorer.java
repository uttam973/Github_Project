import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.io.File;


public class RepositoryExplorer {
    public static void exploreRepository(String directoryPath) {
        File repoDirectory = new File(directoryPath);
        if (repoDirectory.exists() && repoDirectory.isDirectory()) {
            System.out.println("Exploring repository...");

            //checking for existence of README file
            if(hasReadmeFile(repoDirectory)) {
                System.out.println("README file found.");

                //extract information from README file
                extractReadmeInformation(repoDirectory);
            } else {
                System.out.println("No README file found.");
                
            }

            //explore the repository and print file names
            exploreDirectory(repoDirectory);
            System.out.println("Exploration completed successfully");
            
            // Generate project instructions
            ProjectInstructionsGenerator.generateInstructions(directoryPath);

        } else {
            
            System.err.println("Repository directory not found");
        }
    }

    public static boolean hasReadmeFile(File directory) {
        System.out.println("listing files...");
        File[] files = directory.listFiles();

        if(files!=null) {
            for(File file : files) {
                if (file.isFile() && file.getName().equalsIgnoreCase("README.md")) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void exploreDirectory(File directory) {
        System.out.println("listing files inside directory");
        File[] files = directory.listFiles();
        if (files!=null) {
            for (File file : files) {
                if(file.isDirectory()) {
                    exploreDirectory(file);
                } else {
                    System.out.println("File: " + file.getName());
                }
            }
        }
    }

    public static void extractReadmeInformation(File directory) {
        System.out.println("Extracting README information");
        File readmeFile = new File(directory, "README.md");


        try (BufferedReader reader= new BufferedReader(new FileReader(readmeFile))) {
            String line;
            System.out.println("Readme content: " );

            //print first few lines of readme
            int linesToPrint=5;
            while ((line=reader.readLine()) != null && linesToPrint-- >0) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading readme file: " + e.getMessage());
        }
    }
}