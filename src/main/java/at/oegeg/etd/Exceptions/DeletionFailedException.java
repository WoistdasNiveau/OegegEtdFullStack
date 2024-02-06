package at.oegeg.etd.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DeletionFailedException extends BaseRuntimeException
{
    public DeletionFailedException(Class type, String identifier)
    {
        super("Object of type " + type.getSimpleName() +  "with identifier" + identifier
                + "could not be deleted.");
    }

}
