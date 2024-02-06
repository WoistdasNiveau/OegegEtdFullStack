package at.oegeg.etd.Exceptions.Handlers;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;


@Component
public class ConstraintViolationHandler
{
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(ConstraintViolationException.class)
    public String HandleConstraintViolationEception(ConstraintViolationException ex)
    {
        return ex.getMessage();
    }
}
