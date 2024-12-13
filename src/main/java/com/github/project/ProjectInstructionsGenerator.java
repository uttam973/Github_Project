import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.body.MethodDeclaration;

public class ProjectInstructionsGenerator {
    // Set to store standard libraries dynamically
    private static Set<String> standardLibraries = new HashSet<>();

    public static void generateInstructions(String directoryPath) {
        ParserConfiguration parserConfiguration = new ParserConfiguration();
        JavaParser javaParser = new JavaParser(parserConfiguration);

        File repoDirectory = new File(directoryPath);

        if (repoDirectory.exists() && repoDirectory.isDirectory()) {
            System.out.println("Generating project instructions...");

            // Check for the existence of a README file
            if (RepositoryExplorer.hasReadmeFile(repoDirectory)) {
                // Extract information from the README file
                RepositoryExplorer.extractReadmeInformation(repoDirectory);
            }

            // Analyze code files to identify entry point and dependencies
            analyzeCodeFiles(repoDirectory, javaParser);

            System.out.println("Project instructions generated.");
        } else {
            System.err.println("Repository directory not found.");
        }
    }

    private static void analyzeCodeFiles(File directory, JavaParser javaParser) {
        System.out.println("Analyzing code files...");

        // Get a list of all files in the directory
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    // Analyze each code file
                    if (isCodeFile(file)) {
                        System.out.println("Analyzing file: " + file.getName());
                        identifyEntryPointAndDependencies(file, javaParser);
                    }
                }
            }
        }
    }

    private static boolean isCodeFile(File file) {
        // Add logic to determine if the file is a code file based on its extension
        return file.getName().endsWith(".java") || file.getName().endsWith(".py");
    }

    private static void identifyEntryPointAndDependencies(File codeFile, JavaParser javaParser) {
        try {
            if (codeFile.getName().endsWith(".java")) {
                // Java file analysis using JavaParser
                analyzeJavaFile(codeFile, javaParser);
            } else if (codeFile.getName().endsWith(".py")) {
                // Python file analysis using PythonInterpreter
                analyzePythonFile(codeFile);
            }

            // Additional logic for identifying dependencies
            identifyDependencies(codeFile);

        } catch (IOException e) {
            System.err.println("Error reading code file: " + e.getMessage());
        }
    }

    private static void analyzeJavaFile(File codeFile, JavaParser javaParser) throws IOException {
    ParseResult<CompilationUnit> parseResult = javaParser.parse(codeFile);
    if (parseResult.isSuccessful()) {
        CompilationUnit cu = parseResult.getResult().get();

        // Visit and analyze methods in the Java file
        MethodVisitor methodVisitor = new MethodVisitor();
        methodVisitor.visit(cu, null);

        // Check if there are any import declarations present
        boolean hasImports = cu.getImports().isEmpty();

        // If there are import declarations, visit the ImportVisitor
        if (!hasImports) {
            ImportVisitor importVisitor = new ImportVisitor();
            importVisitor.visit(cu, null);
        } else {
            System.out.println("No import declarations found in the Java file.");
        }

        //dependencies from pom.xml file
        String pomXmlPath = codeFile.getParent()+File.separator+"pom.xml";
        if(new File(pomXmlPath).exists()) {
            Set<String> retrieveDependencies = DependencyResolver.retrieveDependenciesFromPomXml(pomXmlPath);
        }


    } else {
        System.err.println("Error parsing Java file: " + parseResult.getProblems());
    }
}

    private static class ImportVisitor extends VoidVisitorAdapter<Void> {
    @Override
public void visit(ImportDeclaration n, Void arg) {
    // Extract library/package names and add them to the set of standard libraries
    String libraryName = n.getNameAsString();
    System.out.println("Library Name : " + libraryName); 
    if (!libraryName.isEmpty()) {
        standardLibraries.add(libraryName);
    } else {
        System.out.println("No import declarations found.");
    }
    super.visit(n, arg);
}
    }

    private static class MethodVisitor extends VoidVisitorAdapter<Void> {
    @Override
    public void visit(MethodDeclaration n, Void arg) {
        // Check if the method is the main method
        if (n.getNameAsString().equals("main") && n.getParameters().size() == 1 &&
                n.getParameters().get(0).getType().asString().equals("String[]")) {
            System.out.println("Entry point identified: main method in " + n.getNameAsString());
        }

        // Additional logic for Java file analysis, if needed

        super.visit(n, arg);
    }
}

    

    private static void analyzePythonFile(File codeFile) throws IOException {
        // Read the Python code from the file
        String pythonCode = new String(Files.readAllBytes(codeFile.toPath()));

        // Check for the if __name__ == "__main__": block as the entry point
        if (pythonCode.contains("if __name__ == '__main__':")) {
            System.out.println("Entry point identified: '__main__' block in " + codeFile.getName());
        }

        // Identify Python dependencies
        // identifyPythonDependencies(pythonCode);
    }

    private static void identifyDependencies(File codeFile) {
        // Add logic to identify dependencies based on the content of the code file
        // This could involve parsing import statements, requirements.txt, etc.
        // Example:
        // System.out.println("Dependencies identified in " + codeFile.getName() + ": " + dependencies);
    }
}
