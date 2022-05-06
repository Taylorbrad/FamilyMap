package Result;

public class FillResult {
    public String message;
    public boolean success;

    /**
     * creates new FillResult object
     * @param message
     * @param success
     */
    public FillResult(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    /**
     * returns message, either an error or success message
     * @return message
     */
    public String getMessage() {
        return message;
    }

    /**
     * sets message (from DB)
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * returns whether the fill was successful or not
     * @return
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * sets the success bool
     * @param success
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }
}
