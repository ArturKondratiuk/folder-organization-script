import java.nio.file.Path;

public class FileInfo {
    private Path path;
    private long size; //bytes
    private String extension;
    private String category;

    public FileInfo(Path path, long size, String extension) {
        this.path = path;
        this.size = size;
        this.extension = extension.toLowerCase();
        this.category = "OTHER"; //default
    }

    //get / set
    public Path getPath() { return path; }
    public long getSize() { return size; }
    public String getExtension() { return extension; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}