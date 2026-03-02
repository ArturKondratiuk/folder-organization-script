import java.util.List;

public class FileCategorizer {
    //supported image extensions
    private final String[] images = {"jpg","jpeg","png","gif","bmp","webp"};

    //supported document extensions
    private final String[] documents = {"pdf","doc","docx","txt","rtf","odt","xls","xlsx","csv","ppt","pptx","json","xml","md", "sln","csproj","java","js","ts","html","css"};

    //supported video extensions
    private final String[] videos = {"mp4","mkv","avi","mov"};

    //categorize files by extension
    public void categorize(List<FileInfo> files) {

        for(FileInfo f : files) {

            String ext = f.getExtension(); //get file extension

            //image category
            if(arrayContains(images, ext)) {
                f.setCategory("IMAGES");
            } 
            
            //document category
            else if(arrayContains(documents, ext)) {
                f.setCategory("DOCUMENTS");
            } 
            
            //video category
            else if(arrayContains(videos, ext)) {
                f.setCategory("VIDEOS");
            } 
            
            //unknown extension
            else {
                f.setCategory("OTHER");
            }
        }
    }

    //check if array contains value (case insensitive)
    private boolean arrayContains(String[] arr, String value) {
        for(String s : arr) {
            if(s.equalsIgnoreCase(value)) return true; //match found
        }
        return false; //no match
    }
}