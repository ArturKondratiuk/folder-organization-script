import java.io.File;
import java.util.*;

public class App {
    //table row format
    private static final String ROW_FORMAT = "%-8s %-35s %12.2f MB %7.2f%%\n";

    public static void main(String[] args) {

        System.out.println("Type help to see available commands\n");

        Scanner input = new Scanner(System.in);

        //start folder = user home
        File currentFolder = new File(System.getProperty("user.home"));

        //validation
        if (!currentFolder.exists() || !currentFolder.isDirectory()) {
            System.out.println("Invalid folder path.");
            return;
        }

        //main loop like real terminal
        while (true) {
            //prompt
            System.out.print(currentFolder.getAbsolutePath() + " > ");
            String command = input.nextLine();

            //exit program
            if (command.equalsIgnoreCase("exit")) {
                break;
            }

            //help commands
            if (command.equalsIgnoreCase("help")) {
                System.out.println("Available commands:");
                System.out.println("ls                 - show folder content/refresh");
                System.out.println("cd <folder>        - go to subfolder");
                System.out.println("cd .               - go to parent folder");
                System.out.println("goto <path>        - go to absolute path");
                System.out.println("summary            - show files summary");
                System.out.println("delete <name>      - delete file or folder");
                System.out.println("rename <old> <new> - rename file or folder (use .filetype)");
                System.out.println("mkdir <name>       - create new directory");
                System.out.println("organize           - sort files by category");
                System.out.println("exit               - close program\n");
                continue;
            }

            //show folder content or refresh
            if (command.equalsIgnoreCase("ls")) {
                printFolderView(currentFolder); //refresh view
                continue;
            }

            //go to parent/subfolder
            if (command.startsWith("cd ")) {
                String folderName = command.substring(3);

                //go to parent
                if (folderName.equals(".")) {
                    File parent = currentFolder.getParentFile();
                    if (parent != null) { //validation if there is no parent folder
                        currentFolder = parent;
                    }
                } 
                
                //go to subfolder
                else {
                    File target = new File(currentFolder, folderName); //folder object

                    if (target.exists() && target.isDirectory()) {
                        currentFolder = target;  //if folder exist goto
                    } 
                    
                    else {
                        System.out.println("Folder not found."); //if there is no folder
                    }
                }

                printFolderView(currentFolder); //auto refresh
                continue;
            }

            //goto absolute path
            if (command.startsWith("goto ")) {

                String newPath = command.substring(5);
                File target = new File(newPath); //input new path in file object

                if (target.exists() && target.isDirectory()) {
                    currentFolder = target;
                    printFolderView(currentFolder); //auto refresh
                } 
                
                else {
                    System.out.println("Invalid path."); //if path is invalid
                }
                continue;
            }

            //delete file or folder
            if (command.startsWith("delete ")) {

                String name = command.substring(7);
                File target = new File(currentFolder, name); //input name of file in file object

                if (!target.exists()) {
                    System.out.println("File not found."); //if not found
                    continue;
                }

                //folder with content confirmation
                if (target.isDirectory()) {

                    File[] content = target.listFiles();

                    if (content != null && content.length > 0) {

                        System.out.print("Folder is not empty.\nDelete folder \"" + name + "\" with " + content.length + " items? (y/n): ");
                        String confirm = input.nextLine();

                        if (!confirm.equalsIgnoreCase("y")) {
                            System.out.println("Delete cancelled.\n");
                            continue;
                        }
                    }
                }
                deleteRecursive(target); //recursive delete
                printFolderView(currentFolder); //refresh
                continue;
            }

            //rename
            if (command.startsWith("rename ")) {
                String[] parts = command.split(" ");

                //validation
                if (parts.length < 3) {
                    System.out.println("Usage: rename <old> <new>");
                    continue;
                }

                File oldFile = new File(currentFolder, parts[1]);
                File newFile = new File(currentFolder, parts[2]);

                if (oldFile.exists()) {
                    oldFile.renameTo(newFile); //rename
                } 
                
                else {
                    System.out.println("File not found.\n"); //if file wasn't found
                }
                printFolderView(currentFolder); //refresh
                continue;
            }

            //create directory
            if (command.startsWith("mkdir ")) {
                String name = command.substring(6);
                File newDir = new File(currentFolder, name); //input directory name
                newDir.mkdir(); //create only one level
                printFolderView(currentFolder);
                continue;
            }

            //organize files by category (by mister gpt)
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

                    //only files from current folder
                    if (!source.getParentFile().equals(currentFolder)) {
                        continue;
                    }

                    String category = f.getCategory();
                    File categoryDir = new File(currentFolder, category);

                    //create category folder if not exists (by calling mkdir method)
                    if (!categoryDir.exists()) {
                        categoryDir.mkdir();
                    }

                    File dest = new File(categoryDir, source.getName());

                    source.renameTo(dest); //move file
                }
                printFolderView(currentFolder);
                continue;
            }

            //summary by categories
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

                //summary output
                System.out.println("\n============= SUMMARY =============");
                System.out.printf("Images:     %5d files    %10.2f MB\n", imagesCount, toMB(imagesSize));
                System.out.printf("Documents:  %5d files    %10.2f MB\n", documentsCount, toMB(documentsSize));
                System.out.printf("Videos:     %5d files    %10.2f MB\n\n", videosCount, toMB(videosSize));
                continue;
            }

            //unknown command
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
        file.delete(); //delete file or empty folder
    }

    //print folder content in table view
    private static void printFolderView(File folder) {
        File[] items = folder.listFiles();

        //empty or access denied
        if (items == null) {
            System.out.println("Folder is empty.");
            return;
        }

        List<File> directories = new ArrayList<>();
        List<File> files = new ArrayList<>();

        //split dirs and files
        for (File f : items) {
            if (f.isDirectory()) {
                directories.add(f);
            } 
            
            else {
                files.add(f);
            }
        }

        //sort by name
        directories.sort(Comparator.comparing(File::getName));
        files.sort(Comparator.comparing(File::getName));

        long totalFolderSize = getFolderSize(folder); //total size

        //folder view output
        System.out.println("\n============================= FOLDER VIEW ===========================\n");
        System.out.printf("%-8s %-35s %12s %7s\n", "TYPE", "NAME", "SIZE", "%");
        System.out.println("---------------------------------------------------------------------");

        //directories
        for (File dir : directories) {
            long size = getFolderSize(dir);
            double percent = totalFolderSize == 0 ? 0 : (size * 100.0 / totalFolderSize);
            System.out.printf(ROW_FORMAT,
                    "<DIR>",
                    fitName(dir.getName(), 35),
                    toMB(size),
                    percent);
        }

        //files
        for (File file : files) {
            long size = file.length();
            double percent = totalFolderSize == 0 ? 0 : (size * 100.0 / totalFolderSize);
            System.out.printf(ROW_FORMAT,
                    getType(file),
                    fitName(file.getName(), 35),
                    toMB(size),
                    percent);
        }

        System.out.println("---------------------------------------------------------------------");

        //total row
        System.out.printf(ROW_FORMAT, "TOTAL", "", toMB(totalFolderSize), 100.0);
        System.out.println("");
    }

    //bytes to mb
    private static double toMB(long bytes) {
        return bytes / (1024.0 * 1024.0);
    }

    //get file extension as type
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

        if (files == null) { //if no files return none
            return 0;
        }

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

    //trim long file names for table view (mister gpt idea)
    private static String fitName(String name, int max) {
        if (name.length() <= max) {
            return name;
        }
        return name.substring(0, max - 3) + "...";
    }
}