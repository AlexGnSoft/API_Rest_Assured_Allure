package api.registration;

public class SuccessRegResponse extends RegistrationRequest {
    private Integer id;
    private String token;

    public SuccessRegResponse() {

    }

    public SuccessRegResponse(String email, String password, Integer id, String token) {
        super(email,password);
        this.id = id;
        this.token = token;
    }

    public Integer getId() {
        return id;
    }


    public String getToken() {
        return token;
    }

}