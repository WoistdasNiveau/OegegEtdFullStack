package at.oegeg.etd.views.Converters;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

public class TelephoneNumberConverter implements Converter<Double,String>
{

    @Override
    public Result<String> convertToModel(Double s, ValueContext valueContext)
    {
        if (s != null)
        {
        String number = s.toString();
        number = number.replaceAll("\\s", "");
        number = number.replaceAll("\\+", "00");
        return Result.ok(number);
        }
        return Result.ok(null);
    }

    @Override
    public Double convertToPresentation(String s, ValueContext valueContext)
    {
        if(s != null)
        {
            return Double.valueOf(s);
        }
        return null;
    }
}
