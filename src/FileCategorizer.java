import java.util.List;

public class FileCategorizer {

    private final String[] images = {"jpg","jpeg","png","gif","bmp","webp"};
    private final String[] documents = {"pdf","doc","docx","txt","xlsx","pptx"};
    private final String[] videos = {"mp4","mkv","avi","mov"};

    public void categorize(List<FileInfo> files) {
        for(FileInfo f : files) {
            String ext = f.getExtension();

            if(arrayContains(images, ext)) {
                f.setCategory("IMAGES");
            } 
            
            else if(arrayContains(documents, ext)){
                f.setCategory("DOCUMENTS");
            } 
            
            else if(arrayContains(videos, ext)){
                f.setCategory("VIDEOS");
            } 
            
            else {
                f.setCategory("OTHER");
            }
        }
    }

    private boolean arrayContains(String[] arr, String value) {
        for(String s : arr) {
            if(s.equalsIgnoreCase(value)) return true;
        }

        return false;
    }
}