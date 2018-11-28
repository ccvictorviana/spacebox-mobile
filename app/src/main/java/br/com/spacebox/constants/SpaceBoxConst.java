package br.com.spacebox.constants;

public class SpaceBoxConst {
    public static String TOKEN = "TOKEN";
    public static Integer SYNC_INTERVAL = 3000;

    // Config Android
    public static String USER_API_CLIENT_BASE_URL = "http://10.0.2.2:7531/";
    public static String FILE_API_CLIENT_BASE_URL = "http://10.0.2.2:8531/";
    public static String FILE_API_CLIENT_BASE_URL_DOWNLOAD = FILE_API_CLIENT_BASE_URL + "files/download?fileId=";

    // Config Local
//    public static String USER_API_CLIENT_BASE_URL = "http://192.168.1.100:7531/";
//    public static String FILE_API_CLIENT_BASE_URL = "http://192.168.1.100:8531/";

    // Config Heroku
//    public static String USER_API_CLIENT_BASE_URL = "https://spacebox-auth-service.herokuapp.com/";
//    public static String FILE_API_CLIENT_BASE_URL = "https://spacebox-file-service.herokuapp.com/";
}
