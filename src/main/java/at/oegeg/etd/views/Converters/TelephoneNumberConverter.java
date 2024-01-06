package at.oegeg.etd.views.Converters;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

import java.util.regex.Pattern;

public class TelephoneNumberConverter implements Converter<String,String>
{
    String regex = "^[+\\d\\s]+$";
    @Override
    public Result<String> convertToModel(String s, ValueContext valueContext)
    {
        if (s != null && Pattern.compile(regex).matcher(s).matches())
        {
        String number = s.toString();
        number = number.replaceAll("\\s", "");
        number = number.replaceAll("\\+", "00");
        return Result.ok(number);
        }
        return Result.error("Telephone number can only contain integers, + or blank lines!");
    }

    @Override
    public String convertToPresentation(String s, ValueContext valueContext)
    {
        return s;
    }
}
