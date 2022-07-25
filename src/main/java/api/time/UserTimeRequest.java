package api.time;

public class UserTimeRequest {
    private String name;
    private String job;

    public UserTimeRequest() {
        super();
    }

    public UserTimeRequest(String name, String job) {
        this.name = name;
        this.job = job;
    }

    public String getName() {
        return name;
    }

    public String getJob() {
        return job;
    }
}