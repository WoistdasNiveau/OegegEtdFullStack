package at.oegeg.etd.views.Forms;

import at.oegeg.etd.DataTransferObjects.DisplayModels.WorkDisplay;
import at.oegeg.etd.Entities.Enums.Priorities;
import at.oegeg.etd.Entities.UserEntity;
import at.oegeg.etd.Repositories.IUserEntityRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.util.ArrayList;
import java.util.List;

public class WorkForm extends FormLayout
{
    // == binders ==
    Binder<WorkDisplay> binder = new BeanValidationBinder<>(WorkDisplay.class);

    // == view fields ==
    ComboBox<UserEntity> userEntityComboBox = new ComboBox<>("Responsible Person");
    TextField descriptionField = new TextField();
    Select<Priorities> prioritiesSelect = new Select<>();

    // == private fields ==
    private final IUserEntityRepository _userRepository;
    // == constructor ==

    public WorkForm(IUserEntityRepository userRepository)
    {
        _userRepository = userRepository;

        UpdateUsers();
        ConfigurePriorities();
        descriptionField.setLabel("Description");

        add(
                new H1("Work"),
                userEntityComboBox,
                descriptionField,
                prioritiesSelect
        );
    }

    // == private methods ==
    private void UpdateUsers()
    {
        userEntityComboBox.setItems(_userRepository.findAll());
        userEntityComboBox.setItemLabelGenerator(UserEntity::getName);
    }

    private void ConfigurePriorities()
    {
        prioritiesSelect.setRenderer(CreatePrioritiesRenderer());
        prioritiesSelect.setLabel("Priority");
        prioritiesSelect.setItems(Priorities.values());
    }
    private static ComponentRenderer<Span, Priorities> CreatePrioritiesRenderer()
    {
        return new ComponentRenderer<>(priorities ->
        {
            Span priorityNone = new Span(Priorities.NONE.name());
            priorityNone.getElement().getThemeList().add("badge contrast primary");
            Span result = priorityNone;
            switch (priorities)
            {
                case NONE ->
                {
                    break;
                }
                case LOW ->
                {
                    Span priorityLow = new Span(Priorities.LOW.name());
                    priorityLow.getElement().getThemeList().add("badge success primary");
                    result = priorityLow;
                }
                case MEDIUM ->
                {
                    Span priorityMedium = new Span(Priorities.MEDIUM.name());
                    priorityMedium.getElement().getThemeList().add("badge primary");
                    result = priorityMedium;
                }
                case HIGH ->
                {
                    Span priorityHigh = new Span(Priorities.HIGH.name());
                    priorityHigh.getElement().getThemeList().add("badge error primary");
                    result = priorityHigh;
                }
                case DONE ->
                {
                    Span priorityDone = new Span(Priorities.DONE.name());
                    priorityDone.getElement().getThemeList().add("badge contrast primary");
                    result = priorityDone;
                }
            }
            return result;
        });
    }
}
