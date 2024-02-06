package at.oegeg.etd.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
public class SavingFailedException extends BaseRuntimeException
{
    public SavingFailedException()
    {
        super("One or more images could not be saved!");
    }

    public SavingFailedException(Exception ex)
    {
        super(ex.getLocalizedMessage());
    }
}
