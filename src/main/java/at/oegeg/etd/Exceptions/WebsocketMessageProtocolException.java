package at.oegeg.etd.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class WebsocketMessageProtocolException extends BaseRuntimeException
{

    public WebsocketMessageProtocolException(String text)
    {
        super(text);
    }
}
