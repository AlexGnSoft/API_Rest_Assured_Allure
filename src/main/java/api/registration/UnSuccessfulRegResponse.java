package api.registration;

public class UnSuccessfulRegResponse extends RegistrationRequest {
    private String error;

    public UnSuccessfulRegResponse() {

    }

    public UnSuccessfulRegResponse(String email, String password, String error) {
        super(email, password);
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
