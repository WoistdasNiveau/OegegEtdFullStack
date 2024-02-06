package at.oegeg.etd.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class NoChatAdminException extends BaseRuntimeException
{
    public NoChatAdminException(String username, String chatTitle)
    {
        super("User with name " + username +  "is not an admin of the chat " + chatTitle + " !");
    }
}
