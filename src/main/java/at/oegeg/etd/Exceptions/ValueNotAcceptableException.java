package at.oegeg.etd.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class ValueNotAcceptableException extends BaseRuntimeException
{

    public ValueNotAcceptableException(String value, String propertyName, Class className)
    {
        super("Value " + value + " is not acceptable for the property " +
        propertyName + " of class " + className.getSimpleName());
    }
}
