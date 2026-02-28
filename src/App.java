import java.util.List;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        System.out.println("Enter folder path to scan:");
        String path = input.nextLine();

        FileScanner scanner = new FileScanner();
        List<FileInfo> files = scanner.scan(path);

        FileCategorizer categorizer = new FileCategorizer();
        categorizer.categorize(files);

        long imagesSize = 0;
        long documentsSize = 0;
        long videosSize = 0;

        int imagesCount = 0;
        int documentsCount = 0;
        int videosCount = 0;

        System.out.println("\nOTHER FILES:");

        for (FileInfo file : files) {

            if (file.getCategory().equals("IMAGES")) {
                imagesCount++;
                imagesSize += file.getSize();
            }

            else if (file.getCategory().equals("DOCUMENTS")) {
                documentsCount++;
                documentsSize += file.getSize();
            }

            else if (file.getCategory().equals("VIDEOS")) {
                videosCount++;
                videosSize += file.getSize();
            }
            
            else {
                System.out.println(file.getPath());
            }
        }

        double imagesMB = imagesSize / (1024.0 * 1024.0);
        double documentsMB = documentsSize / (1024.0 * 1024.0);
        double videosMB = videosSize / (1024.0 * 1024.0);

        System.out.println("\nFILES SUMMARY:\n");

        System.out.println("Images:");
        System.out.println("Number of files: " + imagesCount);
        System.out.println("Total size: " + String.format("%.2f", imagesMB) + " MB\n");

        System.out.println("Documents:");
        System.out.println("Number of files: " + documentsCount);
        System.out.println("Total size: " + String.format("%.2f", documentsMB) + " MB\n");

        System.out.println("Videos:");
        System.out.println("Number of files: " + videosCount);
        System.out.println("Total size: " + String.format("%.2f", videosMB) + " MB\n");

        System.out.println("Total files scanned: " + files.size());

        input.close();
    }
}