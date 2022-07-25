package constants;

public class Constants {
    private final static String BASE_URL = "https://reqres.in/";
    private final static String LIST_OF_USERS_QUERY = "api/users?page=2";
    private final static String SUCCESSFUL_REG_QUERY = "api/register";
    private final static String UNSUCCESSFUL_REG_QUERY = "api/register";
    private final static String LIST_RESOURCES_QUERY = "api/unknown";
    private final static String DELETE_QUERY = "api/users/2";
    private final static String UPDATE_QUERY = "api/users/2";


    public static String getBaseUrl(){
        return BASE_URL;
    }

    public static String getListOfUsersQuery(){
        return LIST_OF_USERS_QUERY;
    }

    public static String getSuccessfulRegQuery(){
        return SUCCESSFUL_REG_QUERY;
    }

    public static String getUnsuccessfulRegQuery(){
        return UNSUCCESSFUL_REG_QUERY;
    }

    public static String getListResourcesQuery(){
        return LIST_RESOURCES_QUERY;
    }

    public static String getDeleteQuery(){
        return DELETE_QUERY;
    }

    public static String getUpdateQuery(){
        return UPDATE_QUERY;
    }
}
