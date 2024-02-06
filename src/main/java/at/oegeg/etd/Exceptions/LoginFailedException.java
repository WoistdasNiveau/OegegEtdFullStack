package at.oegeg.etd.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class LoginFailedException extends BaseRuntimeException
{
    public LoginFailedException(String identifier)
    {
        super("Authentication for identifier " + identifier + "failed.");
    }
}
