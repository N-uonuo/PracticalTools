import java.io.*;

public class SplitWordsToLines {

    public static void main(String[] args) {
        // 定义输入和输出文件的路径
        String inputFile = "C:\\Users\\zhangnianshu\\Desktop\\词书1.txt";
        String outputFile = "C:\\Users\\zhangnianshu\\Desktop\\词书1-1.txt";

        // 调用函数进行文件处理
        splitWords(inputFile, outputFile);
    }

    public static void splitWords(String inputFile, String outputFile) {
        BufferedReader reader = null;
        BufferedWriter writer = null;

        try {
            // 创建文件读取器和写入器
            reader = new BufferedReader(new FileReader(inputFile));
            writer = new BufferedWriter(new FileWriter(outputFile));

            String line;
            // 按行读取文件内容
            while ((line = reader.readLine()) != null) {
                // 通过空格将每一行的单词分隔开
                String[] words = line.split("\\s+");

                // 将每个单词写入新行
                for (String word : words) {
                    writer.write(word);
                    writer.newLine(); // 写入换行符
                }
            }

            System.out.println("处理完成，结果已写入: " + outputFile);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭文件流
                if (reader != null)
                    reader.close();
                if (writer != null)
                    writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
