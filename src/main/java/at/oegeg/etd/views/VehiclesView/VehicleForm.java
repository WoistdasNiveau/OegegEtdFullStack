package at.oegeg.etd.views.VehiclesView;

import at.oegeg.etd.DataTransferObjects.Request.VehicleRequest;
import at.oegeg.etd.DataTransferObjects.Response.VehicleResponse;
import at.oegeg.etd.Entities.VehicleEntity;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import org.springframework.context.annotation.Bean;

public class VehicleForm extends FormLayout
{
    // == Binders ==
    Binder<VehicleResponse> binder = new BeanValidationBinder<>(VehicleResponse.class);
    // == view fields ==
    TextField type = new TextField("Type");
    TextField number = new TextField("Type");
    TextField status = new TextField("Type");
    TextField stand = new TextField("Type");
    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button cancel = new Button("Cancel");

    // == private fields ==
    private VehicleResponse vehicle;

    public VehicleForm()
    {
        binder.bindInstanceFields(this);
        addClassName("createVehicleForm");

        add(type,number,status,stand,createButtonlayout());
    }

    // == public methods ==
    public void SetVehicle(VehicleResponse vehicle)
    {
        this.vehicle = vehicle;
        binder.readBean(vehicle);
    }

    // == private methods ==
    private Component createButtonlayout()
    {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickListener(event -> ValidateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, vehicle)));
        cancel.addClickListener(event -> fireEvent(new CloseEvent(this)));

        save.addClickShortcut(Key.ENTER);
        cancel.addClickShortcut(Key.ESCAPE);

        HorizontalLayout layout = new HorizontalLayout(save,cancel);
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

    // == events ==
    public static abstract class VehicleFormEvent extends ComponentEvent<VehicleForm>
    {
        private VehicleResponse vehicle;
        protected VehicleFormEvent(VehicleForm source, VehicleResponse vehicle)
        {
            super(source,false);
            this.vehicle = vehicle;
        }

        public VehicleRequest getVehicle()
        {
            return VehicleRequest.builder()
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
        SaveEvent(VehicleForm source, VehicleResponse response)
        {
            super(source,response);
        }
    }

    public static class DeleteEvent extends VehicleFormEvent
    {
        DeleteEvent(VehicleForm source, VehicleResponse response)
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
