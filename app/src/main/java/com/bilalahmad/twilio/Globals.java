package com.bilalahmad.twilio;

public class Globals {

    /**
     * You can get these variable values from TWILIO
     * https://www.twilio.com/console
     */
    public static final String ACCOUNT_SID = "AC5d9a25d04cde4b4ba378ca620ff12eeb";
    public static final String AUTH_TOKEN = "5dfd9952db1ca1c091651e91d0d9a41f";
    public static final String FROM_NUMBER = "+18646488898";

    /**
     * How many times request must retry before throwing error
     */
    public static final int RETRY_COUNT = 2;

    /**
     * Few errors
     */
    public static final String INTERNET_ERROR_TITLE = "No internet";
    public static final String INTERNET_ERROR = "It seems you're offline, please connect to wifi or turn on data, then try again.";
    public static final String EMPTY_ERROR = "";


    /**
     * HTTP CODES FOR USER FRIENDLY APPLICATIONS
     */
    public static String[][] HTTP_CODES = new String[][]{
            new String[]{"100", "Continue\nOnly a part of the request has been received by the server, but as long as it has not been rejected, the client should continue with the request."}, // 1x
            new String[]{"101", "Switching\nThe server switches protocol."}, // 1xx (Information)

            new String[]{"200", "OK\nThe request is OK."}, // 2xx (Successful)
            new String[]{"201", "Created\nThe request is complete, and a new resource is created ."}, // 2xx (Successful)
            new String[]{"202", "Accepted\nThe request is accepted for processing, but the processing is not complete."}, // 2xx (Successful)
            new String[]{"203", "Non-authoritative\nThe information in the entity header is from a local or third-party copy, not from the original server."}, // 2xx (Successful)
            new String[]{"204", "No\nA status code and a header are given in the response, but there is no entity-body in the reply."}, // 2xx (Successful)
            new String[]{"205", "Reset\nThe browser should clear the form used for this transaction for additional input."}, // 2xx (Successful)
            new String[]{"206", "Partial\nThe server is returning partial data of the size requested. Used in response to a request specifying a Range header. The server must specify the range included in the response with the Content-Range header."}, // 2xx (Successful)

            new String[]{"300", "Multiple\nA link list. The user can select a link and go to that location. Maximum five addresses  ."}, // 3xx: Redirection
            new String[]{"301", "Moved\nThe requested page has moved to a new url ."}, // 3xx: Redirection
            new String[]{"302", "Found\nThe requested page has moved temporarily to a new url ."}, // 3xx: Redirection
            new String[]{"303", "See\nThe requested page can be found under a different url ."}, // 3xx: Redirection
            new String[]{"304", "Not\nThis is the response code to an If-Modified-Since or If-None-Match header, where the URL has not been modified since the specified date."}, // 3xx: Redirection
            new String[]{"305", "Use\nThe requested URL must be accessed through the proxy mentioned in the Location header."}, // 3xx: Redirection
            new String[]{"306", "Unused\nThis code was used in a previous version. It is no longer used, but the code is reserved."}, // 3xx: Redirection
            new String[]{"307", "Temporary\nThe requested page has moved temporarily to a new url."}, // 3xx: Redirection

            new String[]{"400", "Bad\nThe server did not understand the request."}, // 4xx: Client Error
            new String[]{"401", "Unauthorized\nThe requested page needs a username and a password."}, // 4xx: Client Error
            new String[]{"402", "Payment required\nYou can not use this code yet."}, // 4xx: Client Error
            new String[]{"403", "Forbidden\nAccess is forbidden to the requested page."}, // 4xx: Client Error
            new String[]{"404", "Link not found\nThe server can not find the requested page."}, // 4xx: Client Error
            new String[]{"405", "Method\nThe method specified in the request is not allowed."}, // 4xx: Client Error
            new String[]{"406", "Not\nThe server can only generate a response that is not accepted by the client."}, // 4xx: Client Error
            new String[]{"407", "Proxy\nYou must authenticate with a proxy server before this request can be served."}, // 4xx: Client Error
            new String[]{"408", "Request\nThe request took longer than the server was prepared to wait."}, // 4xx: Client Error
            new String[]{"409", "Conflict\nThe request could not be completed because of a conflict."}, // 4xx: Client Error
            new String[]{"410", "Gone\nThe requested page is no longer available ."}, // 4xx: Client Error
            new String[]{"411", "Length\nThe \"Content-Length\" is not defined. The server will not accept the request without it ."}, // 4xx: Client Error
            new String[]{"412", "Precondition\nThe pre condition given in the request evaluated to false by the server."}, // 4xx: Client Error
            new String[]{"413", "Request\nThe server will not accept the request, because the request entity is too large."}, // 4xx: Client Error
            new String[]{"414", "Request-url\nThe server will not accept the request, because the url is too long. Occurs when you convert a \"post\" request to a \"get\" request with a long query information ."}, // 4xx: Client Error
            new String[]{"415", "Unsupported\nThe server will not accept the request, because the mediatype is not supported ."}, // 4xx: Client Error
            new String[]{"416", "Requested\nThe requested byte range is not available and is out of bounds."}, // 4xx: Client Error
            new String[]{"417", "Expectation\nThe expectation given in an Expect request-header field could not be met by this server."}, // 4xx: Client Error

            new String[]{"500", "Internal Server Error\nThe request was not completed. The server met an unexpected condition."}, // 5xx: Server Error
            new String[]{"501", "Not Supported\nThe request was not completed. The server did not support the functionality required."}, // 5xx: Server Error
            new String[]{"502", "Bad\nThe request was not completed. The server received an invalid response from the upstream server."}, // 5xx: Server Error
            new String[]{"503", "Service\nThe request was not completed. The server is temporarily overloading or down."}, // 5xx: Server Error
            new String[]{"504", "Gateway\nThe gateway has timed out."}, // 5xx: Server Error
            new String[]{"505", "HTTP\nThe server does not support the \"http protocol\" version."}}; // 5xx: Server Error
}

