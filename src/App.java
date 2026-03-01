
import java.io.File;
import java.util.*;

public class App {

    private static final String ROW_FORMAT = "%-8s %-30s %12.2f MB %7.2f%%\n";

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);
        System.out.print("Enter folder path: ");
        String path = input.nextLine();

        File currentFolder = new File(path);

        if (!currentFolder.exists() || !currentFolder.isDirectory()) {
            System.out.println("Invalid folder path.");
            return;
        }

        printFolderView(currentFolder); //first scan

        while (true) {

            System.out.print("\n" + currentFolder.getAbsolutePath() + " > ");
            String command = input.nextLine();

            if (command.equalsIgnoreCase("exit")) {
                break;
            }

            //help
            if (command.equalsIgnoreCase("help")) {
                System.out.println("Available commands:");
                System.out.println("ls               - show folder content");
                System.out.println("cd <folder>      - go to subfolder");
                System.out.println("cd .             - go to parent folder");
                System.out.println("goto <path>      - go to absolute path");
                System.out.println("summary          - show files summary");
                System.out.println("delete <name>     - delete file or folder");
                System.out.println("type <file>       - show file content");
                System.out.println("rename <old> <new>- rename file or folder");
                System.out.println("mkdir <name>      - create new directory");
                System.out.println("organize          - sort files by category");
                System.out.println("exit             - close program");
                continue;
            }

            //ls
            if (command.equalsIgnoreCase("ls")) {
                printFolderView(currentFolder); //reuse method
                continue;
            }

            //cd
            if (command.startsWith("cd ")) {

                String folderName = command.substring(3);

                if (folderName.equals(".")) {
                    File parent = currentFolder.getParentFile();
                    if (parent != null) {
                        currentFolder = parent;
                    }
                } else {
                    File target = new File(currentFolder, folderName);

                    if (target.exists() && target.isDirectory()) {
                        currentFolder = target;
                    } else {
                        System.out.println("Folder not found.");
                    }
                }

                printFolderView(currentFolder); //refresh after cd
                continue;
            }

            //goto
            if (command.startsWith("goto ")) {

                String newPath = command.substring(5);
                File target = new File(newPath);

                if (target.exists() && target.isDirectory()) {
                    currentFolder = target;
                    printFolderView(currentFolder); //refresh after goto
                } else {
                    System.out.println("Invalid path.");
                }

                continue;
            }

            //delete
            if (command.startsWith("delete ")) {

                String name = command.substring(7);
                File target = new File(currentFolder, name);

                if (!target.exists()) {
                    System.out.println("File not found.");
                    continue;
                }

                deleteRecursive(target); //delete folder with content
                printFolderView(currentFolder);
                continue;
            }

            //type
            if (command.startsWith("type ")) {

                String name = command.substring(5);
                File target = new File(currentFolder, name);

                if (!target.exists() || target.isDirectory()) {
                    System.out.println("File not found.");
                    continue;
                }

                try (Scanner reader = new Scanner(target)) {
                    while (reader.hasNextLine()) {
                        System.out.println(reader.nextLine());
                    }
                } catch (Exception e) {
                    System.out.println("Cannot read file.");
                }

                continue;
            }

            //rename
            if (command.startsWith("rename ")) {

                String[] parts = command.split(" ");

                if (parts.length < 3) {
                    System.out.println("Usage: rename <old> <new>");
                    continue;
                }

                File oldFile = new File(currentFolder, parts[1]);
                File newFile = new File(currentFolder, parts[2]);

                if (oldFile.exists()) {
                    oldFile.renameTo(newFile);
                } else {
                    System.out.println("File not found.");
                }

                printFolderView(currentFolder);
                continue;
            }

            //mkdir
            if (command.startsWith("mkdir ")) {

                String name = command.substring(6);
                File newDir = new File(currentFolder, name);
                newDir.mkdir();

                printFolderView(currentFolder);
                continue;
            }

            //organize
            if (command.equalsIgnoreCase("organize")) {

                FileScanner scanner = new FileScanner();
                List<FileInfo> scannedFiles = scanner.scan(currentFolder.getAbsolutePath());

                FileCategorizer categorizer = new FileCategorizer();
                categorizer.categorize(scannedFiles);

                for (FileInfo f : scannedFiles) {

                    File source = f.getPath().toFile();

                    //skip folders
                    if (source.isDirectory()) {
                        continue;
                    }

                    //skip files from subfolders
                    if (!source.getParentFile().equals(currentFolder)) {
                        continue;
                    }

                    String category = f.getCategory();

                    File categoryDir = new File(currentFolder, category);
                    if (!categoryDir.exists()) {
                        categoryDir.mkdir();
                    }

                    File dest = new File(categoryDir, source.getName());

                    source.renameTo(dest); //move file
                }

                printFolderView(currentFolder);
                continue;
            }

            //summary
            if (command.equalsIgnoreCase("summary")) {

                FileScanner scanner = new FileScanner();
                List<FileInfo> scannedFiles = scanner.scan(currentFolder.getAbsolutePath());

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

                continue;
            }

            System.out.println("Unknown command. Type help");
        }
    }

    //delete folder recursively
    private static void deleteRecursive(File file) {

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File child : files) {
                    deleteRecursive(child);
                }
            }
        }

        file.delete();
    }

    //print folder content
    private static void printFolderView(File folder) {

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
            } else {
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
    }

    //bytes to mb
    private static double toMB(long bytes) {
        return bytes / (1024.0 * 1024.0);
    }

    //file type
    private static String getType(File file) {
        String name = file.getName();
        int dot = name.lastIndexOf(".");
        if (dot == -1) {
            return "FILE";
        }
        return name.substring(dot + 1).toUpperCase();
    }

    //recursive folder size
    private static long getFolderSize(File dir) {
        long size = 0;
        File[] files = dir.listFiles();
        if (files == null) {
            return 0;
        }
        for (File f : files) {
            if (f.isFile()) {
                size += f.length();
            } else {
                size += getFolderSize(f);
            }
        }
        return size;
    }
}
