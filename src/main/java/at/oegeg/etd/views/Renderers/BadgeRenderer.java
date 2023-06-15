package at.oegeg.etd.views.Renderers;

import at.oegeg.etd.DataTransferObjects.DisplayModels.UserDisplay;
import at.oegeg.etd.DataTransferObjects.DisplayModels.WorkDisplay;
import at.oegeg.etd.Entities.Enums.Priorities;
import at.oegeg.etd.Entities.Enums.Role;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.renderer.ComponentRenderer;

public class BadgeRenderer
{
    public static ComponentRenderer<Span, Role> RoleRenderer()
    {
        return new ComponentRenderer<>(role ->
        {
            Span roleUser = new Span(Role.USER.name());
            roleUser.getElement().getThemeList().add("badge success pill");
            Span result = roleUser;
            switch (role)
            {
                case LEADER ->
                {
                    Span roleLeader = new Span(Role.LEADER.name().substring(0,1).toUpperCase() + Role.LEADER.name().substring(1).toLowerCase());
                    roleLeader.getElement().getThemeList().add("badge pill");
                    result = roleLeader;
                }
                case ADMIN ->
                {
                    Span roleAdmin = new Span(Role.ADMIN.name().substring(0,1).toUpperCase() + Role.ADMIN.name().substring(1).toLowerCase());
                    roleAdmin.getElement().getThemeList().add("badge error pill");
                    result = roleAdmin;
                }
            }
            return result;
        });
    }

    public static ComponentRenderer<Span, UserDisplay> RoleGridRenderer()
    {
        return new ComponentRenderer<>(r ->
        {
            Span roleUser = new Span(Role.USER.name().substring(0,1).toUpperCase() + Role.USER.name().substring(1).toLowerCase());
            roleUser.getElement().getThemeList().add("badge success pill");
            Span result = roleUser;
            switch (r.getRole())
            {
                case LEADER ->
                {
                    Span roleLeader = new Span(Role.LEADER.name().substring(0,1).toUpperCase() + Role.LEADER.name().substring(1).toLowerCase());
                    roleLeader.getElement().getThemeList().add("badge pill");
                    result = roleLeader;
                }
                case ADMIN ->
                {
                    Span roleAdmin = new Span(Role.ADMIN.name().substring(0,1).toUpperCase() + Role.ADMIN.name().substring(1).toLowerCase());
                    roleAdmin.getElement().getThemeList().add("badge error pill");
                    result = roleAdmin;
                }
            }
            return result;
        });
    }

    public static ComponentRenderer<Span, WorkDisplay> CreatePrioritiesRenderer()
    {
        return new ComponentRenderer<>(workDisplay ->
        {
            Span priorityNone = new Span(Priorities.NONE.name());
            priorityNone.getElement().getThemeList().add("badge pill contrast");
            Span result = priorityNone;
            switch (workDisplay.getPriority())
            {
                case NONE ->
                {
                    break;
                }
                case LOW ->
                {
                    Span priorityLow = new Span(Priorities.LOW.name());
                    priorityLow.getElement().getThemeList().add("badge success pill primary");
                    result = priorityLow;
                }
                case MEDIUM ->
                {
                    Span priorityMedium = new Span(Priorities.MEDIUM.name());
                    priorityMedium.getElement().getThemeList().add("badge pill primary");
                    result = priorityMedium;
                }
                case HIGH ->
                {
                    Span priorityHigh = new Span(Priorities.HIGH.name());
                    priorityHigh.getElement().getThemeList().add("badge error pill primary");
                    result = priorityHigh;
                }
                case DONE ->
                {
                    Span priorityDone = new Span(Priorities.DONE.name());
                    priorityDone.getElement().getThemeList().add("badge contrast pill primary");
                    result = priorityDone;
                }
            }
            return result;
        });
    }
}
