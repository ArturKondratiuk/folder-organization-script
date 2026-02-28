import java.io.File;
import java.util.*;

public class App {

    private static final String ROW_FORMAT = "%-8s %-30s %12.2f MB %7.2f%%\n";

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);
        System.out.print("Enter folder path: ");
        String path = input.nextLine();

        File folder = new File(path);

        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Invalid folder path.");
            return;
        }

        File[] items = folder.listFiles();

        if (items == null) {
            System.out.println("Folder is empty.");
            return;
        }

        List<File> directories = new ArrayList<>();
        List<File> files = new ArrayList<>();

        for (File f : items) {
            if (f.isDirectory()) {
                directories.add(f);
            } 
            
            else {
                files.add(f);
            }
        }

        directories.sort(Comparator.comparing(File::getName));
        files.sort(Comparator.comparing(File::getName));

        long totalFolderSize = getFolderSize(folder);

        System.out.println("\n========================= FOLDER VIEW ========================\n");

        System.out.printf("%-8s %-30s %12s %7s\n", "TYPE", "NAME", "SIZE", "%");
        System.out.println("-----------------------------------------------------------------");

        //directories
        for (File dir : directories) {
            long size = getFolderSize(dir);
            double percent = totalFolderSize == 0 ? 0 : (size * 100.0 / totalFolderSize);
            System.out.printf(ROW_FORMAT, "<DIR>", dir.getName(), toMB(size), percent);
        }

        //files
        for (File file : files) {
            long size = file.length();
            double percent = totalFolderSize == 0 ? 0 : (size * 100.0 / totalFolderSize);
            System.out.printf(ROW_FORMAT, getType(file), file.getName(), toMB(size), percent);
        }

        System.out.println("-----------------------------------------------------------------");
        System.out.printf("%-8s %-30s %12.2f MB %7s\n\n", "TOTAL", "", toMB(totalFolderSize), "100%");

        //summary

        FileScanner scanner = new FileScanner();
        List<FileInfo> scannedFiles = scanner.scan(path);

        FileCategorizer categorizer = new FileCategorizer();
        categorizer.categorize(scannedFiles);

        long imagesSize = 0, documentsSize = 0, videosSize = 0;
        int imagesCount = 0, documentsCount = 0, videosCount = 0;

        for (FileInfo f : scannedFiles) {
            switch (f.getCategory()) {
                case "IMAGES":
                    imagesSize += f.getSize();
                    imagesCount++;
                    break;
                case "DOCUMENTS":
                    documentsSize += f.getSize();
                    documentsCount++;
                    break;
                case "VIDEOS":
                    videosSize += f.getSize();
                    videosCount++;
                    break;
            }
        }

        System.out.println("\n============= SUMMARY =============");

        System.out.printf("Images:     %5d files    %10.2f MB\n", imagesCount, toMB(imagesSize));
        System.out.printf("Documents:  %5d files    %10.2f MB\n", documentsCount, toMB(documentsSize));
        System.out.printf("Videos:     %5d files    %10.2f MB\n", videosCount, toMB(videosSize));
    }

    //bytes to mb
    private static double toMB(long bytes) {
        return bytes / (1024.0 * 1024.0);
    }

    //file type
    private static String getType(File file) {
        String name = file.getName();
        int dot = name.lastIndexOf(".");
        if (dot == -1) return "FILE";
        return name.substring(dot + 1).toUpperCase();
    }

    //recursive folder size
    private static long getFolderSize(File dir) {
        long size = 0;
        File[] files = dir.listFiles();
        if (files == null) return 0;
        for (File f : files) {
            if (f.isFile()) {
                size += f.length();
            } 
            
            else {
                size += getFolderSize(f);
            }
        }
        return size;
    }
}