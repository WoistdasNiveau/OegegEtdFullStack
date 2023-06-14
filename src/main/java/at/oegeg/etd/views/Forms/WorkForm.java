package at.oegeg.etd.views.Forms;

import at.oegeg.etd.DataTransferObjects.DisplayModels.UserDisplay;
import at.oegeg.etd.DataTransferObjects.DisplayModels.WorkDisplay;
import at.oegeg.etd.Entities.Enums.Priorities;
import at.oegeg.etd.Entities.UserEntity;
import at.oegeg.etd.Repositories.IUserEntityRepository;
import at.oegeg.etd.Services.Implementations.UserService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.shared.Registration;

import java.util.stream.Collectors;

public class WorkForm extends FormLayout
{
    // == binders ==
    Binder<WorkDisplay> binder = new BeanValidationBinder<>(WorkDisplay.class);

    // == view fields ==
    ComboBox<String> responsiblePerson = new ComboBox<>("Responsible Person");
    TextField description = new TextField();
    Select<Priorities> priority = new Select<>();
    Button saveButton = new Button();
    public Button deleteButton = new Button();
    Button cancelButton = new Button();

    // == private fields ==
    private final UserService _userService;
    private WorkDisplay selectedWork;

    // == constructor ==
    public WorkForm(UserService userRepository)
    {
        _userService = userRepository;

        binder.forField(description)
                .asRequired()
                .bind( WorkDisplay::getDescription, WorkDisplay::setDescription);
        binder.bind(priority, WorkDisplay::getPriority, WorkDisplay::setPriority);
        binder.bind(responsiblePerson, WorkDisplay::getResponsiblePerson, WorkDisplay::setResponsiblePerson);

        description.addValueChangeListener(t -> SaveEnabled());
        priority.addValueChangeListener(t -> SaveEnabled());

        ConfigureResponsiblePerson();
        ConfigurePriorities();
        description.setLabel("Description");

        add(
                new H1("Work"),
                responsiblePerson,
                description,
                priority,
                CreateButtonLayout()
        );
    }

    // == public methods ==
    public void SetSelectedWork(WorkDisplay workDisplay)
    {
        selectedWork = workDisplay;
        binder.readBean(workDisplay);
    }
    // == private methods ==
    private Component CreateButtonLayout()
    {
        saveButton.setText("Save");
        deleteButton.setText("Delete");
        cancelButton.setText("Cancel");

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        saveButton.addClickListener(event -> ValidateAndSave());
        deleteButton.addClickListener(event -> fireEvent(new DeleteEvent(this, selectedWork)));
        cancelButton.addClickListener(event -> fireEvent(new CloseEvent(this)));

        saveButton.addClickShortcut(Key.ENTER);
        cancelButton.addClickShortcut(Key.ESCAPE);

        return new HorizontalLayout(saveButton, deleteButton, cancelButton);
    }

    private void ValidateAndSave()
    {
        try
        {
            binder.writeBean(selectedWork);
            fireEvent(new SaveEvent(this, selectedWork));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void ConfigureResponsiblePerson()
    {
        responsiblePerson.setItems(_userService.GetAllEnabledUsers().stream().map(UserDisplay::getName).collect(Collectors.toList()));
        responsiblePerson.addValueChangeListener(t -> SaveEnabled());
    }


    private void ConfigurePriorities()
    {
        priority.setRenderer(CreatePrioritiesRenderer());
        priority.setLabel("Priority");
        priority.setItems(Priorities.values());
        priority.setEmptySelectionAllowed(false);
        priority.setRequiredIndicatorVisible(true);
    }

    private void SaveEnabled()
    {
        saveButton.setEnabled(!description.isEmpty());
    }
    private static ComponentRenderer<Span, Priorities> CreatePrioritiesRenderer()
    {
        return new ComponentRenderer<>(priorities ->
        {
            Span priorityNone = new Span(Priorities.NONE.name());
            priorityNone.getElement().getThemeList().add("badge contrast");
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

    // == events ==
    public static abstract class WorkFormEvent extends ComponentEvent<WorkForm>
    {
        private WorkDisplay workDisplay;
        protected WorkFormEvent(WorkForm source, WorkDisplay workDisplay)
        {
            super(source,false);
            this.workDisplay = workDisplay;
        }

        public WorkDisplay getWorkDisplay()
        {
            return workDisplay;
        }
    }

    public static class SaveEvent extends WorkFormEvent
    {
        SaveEvent(WorkForm source, WorkDisplay response)
        {
            super(source,response);
        }
    }

    public static class DeleteEvent extends WorkFormEvent
    {
        DeleteEvent(WorkForm source, WorkDisplay response)
        {
            super(source,response);
        }
    }

    public static class CloseEvent extends WorkFormEvent
    {
        CloseEvent(WorkForm source)
        {
            super(source,null);
        }
    }

    public <T extends ComponentEvent<?>> Registration AddListener(Class<T> eventType, ComponentEventListener<T> listener)
    {
        return getEventBus().addListener(eventType,listener);
    }
}
