import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    public void saveToFile(String filename, List<String> data) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (String line : data) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> loadFromFile(String filename) {
        ArrayList<String> data = new ArrayList<>();
        File file = new File(filename);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                data.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public void appendToFile(String fileName, List<String> data) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, true))) {
            for (String line : data) {
                writer.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeToFile(String fileName, List<String> data) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (String line : data) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
