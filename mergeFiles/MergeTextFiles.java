package tools.mergeFiles;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class MergeTextFiles {
  public static void main(String[] args) {
    // 输入文件夹路径
    String inputFolderPath = "C:\\Users\\zhangnianshu\\Desktop\\新建文件夹";
    // 输出文件路径
    String outputFilePath = "C:\\Users\\zhangnianshu\\Desktop\\zong.txt";// 注意输出文件地址不可以和输入文件夹地址一样

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
      // 获取输入文件夹中的所有.txt文件
      List<Path> textFiles = Files.walk(Paths.get(inputFolderPath))
          .filter(Files::isRegularFile)
          .filter(path -> path.toString().endsWith(".txt"))
          .toList();

      System.out.println("Number of files to merge: " + textFiles.size());

      for (Path filePath : textFiles) {
        System.out.println("Processing file: " + filePath);
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
          String line;
          while ((line = reader.readLine()) != null) {
            writer.write(line);
            writer.newLine();
          }
        } catch (IOException e) {
          System.err.println("Error reading file: " + filePath);
          e.printStackTrace();
        }
      }
      System.out.println("All files have been successfully merged into " + outputFilePath);
    } catch (IOException e) {
      System.err.println("Error writing to output file: " + outputFilePath);
      e.printStackTrace();
    }
  }
}
