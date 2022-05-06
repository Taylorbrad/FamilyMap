package Result;

public class LoadResult {
    public String message;
    public boolean success;

    /**
     * creates new LoadResult object
     * @param message
     * @param success
     */
    public LoadResult(String message, boolean success) {
        this.message = message;
        this.success = success;
    }
}
