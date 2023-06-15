package at.oegeg.etd.views.Renderers;

import at.oegeg.etd.DataTransferObjects.DisplayModels.UserDisplay;
import at.oegeg.etd.Entities.Enums.Role;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import org.springframework.security.core.userdetails.User;

public class IsEnabledRenderer
{
    public static ComponentRenderer<Icon, UserDisplay> EnabledRenderer()
    {
        return new ComponentRenderer<Icon, UserDisplay>(t ->
        {
            Icon icon = CreateIcon(VaadinIcon.CLOSE_SMALL, "Cancelled");
            icon.getElement().getThemeList().add("badge error");
            if (t.isEnabled())
            {
                icon = CreateIcon(VaadinIcon.CHECK, "Confirmed");
                icon.getElement().getThemeList().add("badge success");
            }
            return icon;
        });
    }

    private static Icon CreateIcon(VaadinIcon vaadinIcon, String label)
    {
        Icon icon = vaadinIcon.create();
        icon.getStyle().set("padding", "var(--lomu-space-xs");

        icon.getElement().setAttribute("aria-label", label);
        icon.getElement().setAttribute("title", label);
        return icon;
    }
}
