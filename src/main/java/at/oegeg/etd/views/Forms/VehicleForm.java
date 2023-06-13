package at.oegeg.etd.views.Forms;

import at.oegeg.etd.DataTransferObjects.DisplayModels.VehicleDisplay;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

public class VehicleForm extends FormLayout
{
    // == Binders ==
    Binder<VehicleDisplay> binder = new BeanValidationBinder<>(VehicleDisplay.class);
    // == view fields ==
    TextField type = new TextField("Type");
    TextField number = new TextField("Number");
    TextField status = new TextField("Status");
    TextField stand = new TextField("Stand");
    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button cancel = new Button("Cancel");

    // == private fields ==
    private VehicleDisplay vehicle;
    private boolean deleteEnabled = false;

    public VehicleForm(boolean deleteEnabled)
    {
        this.deleteEnabled = deleteEnabled;
        //binder.bindInstanceFields(this);
        addClassName("createVehicleForm");

        ConfigureBinder();
        ConfigureTextFields();


        add(type,number,status,stand,createButtonlayout());
    }

    // == public methods ==
    public void SetVehicle(VehicleDisplay vehicle)
    {
        this.vehicle = vehicle;
        binder.readBean(vehicle);
    }

    public void SetDeleteEnabled(boolean bool)
    {
        this.deleteEnabled = bool;
    }

    // == private methods ==
    private void ConfigureBinder()
    {
        binder.forField(type)
                .asRequired()
                .bind(VehicleDisplay::getType, VehicleDisplay::setType);
        binder.forField(number)
                .asRequired()
                .bind(VehicleDisplay::getNumber, VehicleDisplay::setNumber);
        binder.forField(status)
                .asRequired()
                .bind(VehicleDisplay::getStatus, VehicleDisplay::setStatus);
        binder.forField(stand)
                .asRequired()
                .bind(VehicleDisplay::getStand, VehicleDisplay::setStand);
    }

    private void ConfigureTextFields()
    {
        type.addValueChangeListener(t -> SaveEnabled());
        type.setRequired(true);

        number.addValueChangeListener(t -> SaveEnabled());
        number.setRequired(true);

        status.addValueChangeListener(t -> SaveEnabled());
        status.setRequired(true);

        stand.addValueChangeListener(t -> SaveEnabled());
        stand.setRequired(true);
    }

    private Component createButtonlayout()
    {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickListener(event -> ValidateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, vehicle)));
        cancel.addClickListener(event -> fireEvent(new CloseEvent(this)));

        save.addClickShortcut(Key.ENTER);
        cancel.addClickShortcut(Key.ESCAPE);

        HorizontalLayout layout;
        if(deleteEnabled)
        {
            layout = new HorizontalLayout(save, delete, cancel);
        }
        else
        {
            layout = new HorizontalLayout(save, cancel);
        }
        return layout;
    }

    private void ValidateAndSave()
    {
        try
        {
            binder.writeBean(vehicle);
            fireEvent(new SaveEvent(this, vehicle));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void SaveEnabled()
    {
        save.setEnabled(!type.isEmpty() && !number.isEmpty() && !stand.isEmpty() && !status.isEmpty());
    }

    // == events ==
    public static abstract class VehicleFormEvent extends ComponentEvent<VehicleForm>
    {
        private VehicleDisplay vehicle;
        protected VehicleFormEvent(VehicleForm source, VehicleDisplay vehicle)
        {
            super(source,false);
            this.vehicle = vehicle;
        }

        public VehicleDisplay getVehicle()
        {
            return VehicleDisplay.builder()
                    .identifier(vehicle.getIdentifier())
                    .number(vehicle.getNumber())
                    .stand(vehicle.getStand())
                    .status(vehicle.getStatus())
                    .type(vehicle.getType())
                    .build();
        }
    }

    public static class SaveEvent extends VehicleFormEvent
    {
        SaveEvent(VehicleForm source, VehicleDisplay response)
        {
            super(source,response);
        }
    }

    public static class DeleteEvent extends VehicleFormEvent
    {
        DeleteEvent(VehicleForm source, VehicleDisplay response)
        {
            super(source,response);
        }
    }

    public static class CloseEvent extends VehicleFormEvent
    {
        CloseEvent(VehicleForm source)
        {
            super(source,null);
        }
    }

    public <T extends ComponentEvent<?>>Registration AddListener(Class<T> eventType, ComponentEventListener<T> listener)
    {
        return getEventBus().addListener(eventType,listener);
    }
}
