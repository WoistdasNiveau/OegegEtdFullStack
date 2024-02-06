package at.oegeg.etd.Exceptions;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.security.auth.login.LoginException;

//@ControllerAdvice //uncomment to use this handler
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler
{
    @ExceptionHandler(value = {IllegalArgumentException.class, IllegalStateException.class, ConstraintViolationException.class, LoginException.class})
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request)
    {
        String body = ex.getCause().toString();
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }
}
