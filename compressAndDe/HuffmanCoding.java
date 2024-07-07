package tools.compressAndDe;

import java.io.*;
//import java.nio.charset.StandardCharsets;
import java.util.*;

//how to use:
//java tools.compressAndDe.HuffmanCoding compress "input file address" "new output file address"
//java tools.compressAndDe.HuffmanCoding decompress "output file address" "new input file address"

public class HuffmanCoding {

    private String[] huffmanCodes = new String[256]; // 用于存储每个字符的编码

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        if (args.length < 3) {
            System.out.println("Usage: java huffman.hCoding <compress/decompress> <inputFilePath> <outputFilePath>");
            return;
        }

        String mode = args[0];
        String inputFilePath = args[1];
        String outputFilePath = args[2];

        HuffmanCoding hc = new HuffmanCoding();

        long startTime = System.currentTimeMillis();
        if (mode.equalsIgnoreCase("compress")) {
            hc.compressFile(inputFilePath, outputFilePath);
        } else if (mode.equalsIgnoreCase("decompress")) {
            hc.decompressFile(inputFilePath, outputFilePath);
        } else {
            System.out.println("Invalid mode. Use 'compress' or 'decompress'.");
            return;
        }
        long endTime = System.currentTimeMillis();

        System.out.println((mode.equalsIgnoreCase("compress") ? "Compression" : "Decompression") + " time: "
                + (endTime - startTime) + "ms");
    }

    public void compressFile(String inputFilePath, String outputFilePath) throws IOException {
        int[] charFreqs = new int[256];
        byte[] fileData;
        try (FileInputStream fis = new FileInputStream(inputFilePath)) {
            fileData = fis.readAllBytes();
        }

        for (byte b : fileData) {
            charFreqs[b & 0xFF]++;
        }

        HuffmanTree tree = new HuffmanTree();
        Node root = tree.buildTree(charFreqs);
        generateCodes(root, "");

        StringBuilder encodedData = new StringBuilder();
        for (byte b : fileData) {
            encodedData.append(huffmanCodes[b & 0xFF]);
        }

        BitSet bitSet = new BitSet(encodedData.length());
        for (int i = 0; i < encodedData.length(); i++) {
            if (encodedData.charAt(i) == '1') {
                bitSet.set(i);
            }
        }

        String fileExtension = getFileExtension(inputFilePath);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(outputFilePath))) {
            oos.writeObject(bitSet);
            oos.writeObject(huffmanCodes);
            oos.writeObject(fileExtension);
            System.out.println("Compression successful: " + outputFilePath);
        } catch (IOException e) {
            System.out.println("Error writing compressed file: " + e.getMessage());
        }
    }

    public void decompressFile(String inputFilePath, String outputFilePath) throws IOException, ClassNotFoundException {
        BitSet bitSet;
        String[] huffmanCodes;
        String fileExtension;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(inputFilePath))) {
            bitSet = (BitSet) ois.readObject();
            huffmanCodes = (String[]) ois.readObject();
            fileExtension = (String) ois.readObject();
        }

        StringBuilder encodedData = new StringBuilder();
        for (int i = 0; i < bitSet.length(); i++) {
            encodedData.append(bitSet.get(i) ? '1' : '0');
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < encodedData.length(); i++) {
            temp.append(encodedData.charAt(i));
            for (int j = 0; j < huffmanCodes.length; j++) {
                if (huffmanCodes[j] != null && huffmanCodes[j].equals(temp.toString())) {
                    byteArrayOutputStream.write(j);
                    temp.setLength(0);
                    break;
                }
            }
        }

        byte[] decodedBytes = byteArrayOutputStream.toByteArray();

        // Debug: Print the decoded bytes length
        System.out.println("Decoded bytes length: " + decodedBytes.length);

        // 自动添加原始文件的扩展名
        if (!outputFilePath.toLowerCase().endsWith(fileExtension)) {
            outputFilePath += fileExtension;
        }

        try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
            fos.write(decodedBytes);
            System.out.println("Decompression successful: " + outputFilePath);
        } catch (IOException e) {
            System.out.println("Error writing decompressed file: " + e.getMessage());
        }
    }

    private String getFileExtension(String filePath) {
        int lastDotIndex = filePath.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filePath.length() - 1) {
            return filePath.substring(lastDotIndex);
        } else {
            return ""; // 如果没有扩展名
        }
    }

    private void generateCodes(Node node, String code) {
        if (node == null)
            return;

        if (node.left == null && node.right == null) {
            huffmanCodes[node.ch & 0xFF] = code;
        }

        generateCodes(node.left, code + '0');
        generateCodes(node.right, code + '1');
    }
}

class Node implements Serializable {
    char ch;
    int frequency;
    Node left, right;

    Node(char ch, int frequency, Node left, Node right) {
        this.ch = ch;
        this.frequency = frequency;
        this.left = left;
        this.right = right;
    }

    boolean isLeaf() {
        return (left == null) && (right == null);
    }
}

class HuffmanTree {
    public Node buildTree(int[] charFreqs) {
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(n -> n.frequency));

        for (char i = 0; i < charFreqs.length; i++) {
            if (charFreqs[i] > 0) {
                Node node = new Node(i, charFreqs[i], null, null);
                queue.add(node);
            }
        }

        while (queue.size() > 1) {
            Node left = queue.poll();
            Node right = queue.poll();

            Node parent = new Node('\0', left.frequency + right.frequency, left, right);
            queue.add(parent);
        }

        return queue.poll();
    }
}
