import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class FileScanner {
    //scan directory recursively and return list of FileInfo
    public List<FileInfo> scan(String dirPath) {

        List<FileInfo> files = new ArrayList<>();

        //starting path
        Path startPath = Paths.get(dirPath);

        try {
            //walk through file tree recursively
            Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {

                    try {
                        //get file size
                        long size = Files.size(file);

                        String name = file.getFileName().toString();
                        String ext = "";

                        //extract extension
                        int dot = name.lastIndexOf('.');
                        if (dot > 0 && dot < name.length() - 1) {
                            ext = name.substring(dot + 1);
                        }

                        //add file info to list
                        files.add(new FileInfo(file, size, ext));
                    } 
                    
                    catch (IOException e) {
                        //cannot read file
                        System.out.println("Cannot read file: " + file);
                    }
                    return FileVisitResult.CONTINUE; //continue scanning
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    //skip inaccessible files or folders
                    System.out.println("Access denied: " + file);
                    return FileVisitResult.SKIP_SUBTREE;
                }
            });
        } 
        
        catch (IOException e) {
            //general scanning error
            System.out.println("Error scanning directory: " + e.getMessage());
        }
        return files; //return scanned files list
    }
}