package com.hunter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.lang.ProcessBuilder;

public class SpringBuilder {

  public static void main(String[] args) {
    int exitCode = 0;
    if (args.length < 1) {
      System.out.println(
          "Invalid arguments. Please provide a single string representing the name of the project you would like to create.");
      exitCode = -1;
      System.exit(exitCode);
    }
    String projectName = args[0];

    try {
      createNestedDirectories(projectName + "/src/main/java/com/hunter/" + projectName);
      createNestedDirectories(projectName + "/src/main/resources");
      createNestedDirectories(projectName + "/src/main/java/com/hunter/" + projectName + "/dto");
      createNestedDirectories(projectName + "/src/main/java/com/hunter/" + projectName + "/enums");
      createNestedDirectories(projectName + "/src/main/java/com/hunter/" + projectName + "/controller");
      createNestedDirectories(projectName + "/src/main/java/com/hunter/" + projectName + "/config");
      createNestedDirectories(projectName + "/src/main/java/com/hunter/" + projectName + "/repository");
      createNestedDirectories(projectName + "/src/main/java/com/hunter/" + projectName + "/service");
      createNestedDirectories(projectName + "/src/main/java/com/hunter/" + projectName + "/model");
      createNestedDirectories(projectName + "/src/test/java/com/hunter/" + projectName);
      createPomFile(projectName);
      createMainFile(projectName);
      createReadMe(projectName);
      createGitIgnore(projectName);
      createApplicationPropertiesFile(projectName);
      ProcessBuilder pb = new ProcessBuilder("git", "init");
      pb.directory(new File(getCurrentDir() + "/" + projectName));
      Process init = pb.start();
      exitCode = init.waitFor();
    } catch (IOException | InterruptedException e) {
      exitCode = -1;
      System.err.println(e.getMessage());
      System.exit(exitCode);
    }

    System.out.println("Successfully scaffolded java project!");

    System.exit(exitCode);
  }

  private static void createNestedDirectories(String dirs) throws IOException {
    Path path = Paths.get(getCurrentDir() + "/" + dirs);

    try {
      Files.createDirectories(path);
    } catch (IOException e) {
      System.err.println("Failed to create directory: " + e.getMessage());
      e.printStackTrace();
      throw new IOException("Failed to create directory: " + e.getMessage());
    }
  }

  private static String formatProjectName(String projectName) {
    if (projectName == null || projectName.length() == 0) {
      return projectName;
    }

    return projectName.substring(0, 1).toUpperCase() + projectName.substring(1);
  }

  private static void createPomFile(String projectName) throws IOException {
    Map<String, String> replacements = new HashMap<>();
    replacements.put("{{PROJECT_NAME}}", projectName);
    replacements.put("{{FORMATTED_NAME}}", formatProjectName(projectName));
    String whereTo = getCurrentDir() + "/" + projectName + "/pom.xml";
    String templatePath = "pom.template.xml";
    createFileFromTemplate(templatePath, whereTo, replacements);
  }

  private static void createReadMe(String projectName) throws IOException {
    Map<String, String> replacements = new HashMap<>();
    replacements.put("{{PROJECT_NAME}}", projectName);
    String whereTo = getCurrentDir() + "/" + projectName + "/README.md";
    String templatePath = "README.template.md";
    createFileFromTemplate(templatePath, whereTo, replacements);
  }

  private static void createGitIgnore(String projectName) throws IOException {
    String whereTo = getCurrentDir() + "/" + projectName + "/.gitignore";
    String templatePath = ".gitignore.template";
    createFileFromTemplate(templatePath, whereTo);
  }

  private static void createMainFile(String projectName) throws IOException {
    Map<String, String> replacements = new HashMap<>();
    replacements.put("{{PROJECT_NAME}}", projectName);
    replacements.put("{{FORMATTED_NAME}}", formatProjectName(projectName));
    String whereTo = getCurrentDir() + "/" + projectName + "/src/main/java/com/hunter/" + projectName + "/"
        + formatProjectName(projectName)
        + ".java";
    String templatePath = "Main.java.template";
    createFileFromTemplate(templatePath, whereTo, replacements);
  }

  private static void createApplicationPropertiesFile(String projectName) throws IOException {
    Map<String, String> replacements = new HashMap<>();
    replacements.put("{{PROJECT_NAME}}", projectName);
    String whereTo = getCurrentDir() + "/" + projectName + "/src/main/resources/application.properties";
    String templatePath = "application.properties.template";
    createFileFromTemplate(templatePath, whereTo, replacements);
  }

  private static void createFileFromTemplate(String resourceName, String outputPath) throws IOException {
    Map<String, String> emptyMap = new HashMap<>();
    createFileFromTemplate(resourceName, outputPath, emptyMap);
  }

  private static void createFileFromTemplate(String resourceName, String outputPath, Map<String, String> replacements)
      throws IOException {
    try (InputStream stream = SpringBuilder.class.getClassLoader().getResourceAsStream(resourceName)) {
      if (stream == null) {
        throw new IOException("Resource not found: " + resourceName);
      }

      InputStreamReader isr = new InputStreamReader(stream);
      BufferedReader br = new BufferedReader(isr);

      Path whereTo = Paths.get(outputPath);

      StringBuilder modifiedContent = new StringBuilder();
      String line;

      while ((line = br.readLine()) != null) {
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
          line = line.replace(entry.getKey(), entry.getValue());
        }
        modifiedContent.append(line).append(System.lineSeparator());
      }

      Files.writeString(whereTo, modifiedContent.toString());
    } catch (IOException e) {
      System.err.println("Failed to write pom file: " + e.getMessage());
      e.printStackTrace();
      throw new IOException("Failed to write pom file: " + e.getMessage());
    }
  }

  private static String getCurrentDir() {
    return System.getProperty("user.dir");
  }
}
