package Request;

public class FillRequest {
    public String username;
    public int generation;

    /**
     * creates new FillRequest object
     * @param username
     * @param generation
     */
    public FillRequest(String username, int generation) {
        this.username = username;
        this.generation = generation;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getGeneration() {
        return generation;
    }

    public void setGeneration(int generation) {
        this.generation = generation;
    }
}
