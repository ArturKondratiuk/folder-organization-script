import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class FileScanner {

    public List<FileInfo> scan(String dirPath) {

        List<FileInfo> files = new ArrayList<>();

        Path startPath = Paths.get(dirPath);

        try {
            Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {

                    try {
                        long size = Files.size(file);

                        String name = file.getFileName().toString();
                        String ext = "";

                        int dot = name.lastIndexOf('.');
                        if (dot > 0 && dot < name.length() - 1) {
                            ext = name.substring(dot + 1);
                        }

                        files.add(new FileInfo(file, size, ext));

                    } 
                    
                    catch (IOException e) {
                        System.out.println("Cannot read file: " + file);
                    }

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    System.out.println("Access denied: " + file);
                    return FileVisitResult.SKIP_SUBTREE;
                }
            });

        } 
        
        catch (IOException e) {
            System.out.println("Error scanning directory: " + e.getMessage());
        }

        return files;
    }
}
