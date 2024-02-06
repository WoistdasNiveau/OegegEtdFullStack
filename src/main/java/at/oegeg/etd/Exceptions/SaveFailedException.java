package at.oegeg.etd.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class SaveFailedException extends RuntimeException
{
    public SaveFailedException(Class type, String message)
    {
        super("Could not save class of type " + type.getSimpleName() +" because of: " + message);
    }
}
