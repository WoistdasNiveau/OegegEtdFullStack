package at.oegeg.etd.Exceptions;

public class BaseRuntimeException extends RuntimeException
{
    public BaseRuntimeException(String text)
    {
        super(text);
    }
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this; // Return 'this' to prevent generating the stack trace
    }
}
