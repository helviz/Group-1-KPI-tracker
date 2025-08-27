package org.pahappa.systems.client.converters;

import org.pahappa.systems.kpiTracker.core.services.GoalService;
import org.pahappa.systems.kpiTracker.models.goalMgt.Goal;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter("goalConverter")
public class GoalConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String submittedValue) {
        if (submittedValue == null || submittedValue.trim().isEmpty()) {
            return null;
        }

        try {
            GoalService goalService = ApplicationContextProvider.getBean(GoalService.class);
            return goalService.getInstanceByID(submittedValue);

        } catch (Exception e) {
            // Added error handling for robustness.
            throw new ConverterException("Could not convert '" + submittedValue + "' to a Department.", e);
        }
    }

    /**
     * Converts a Department object into its unique String ID for the HTML dropdown.
     */
    @Override
    public String getAsString(FacesContext context, UIComponent component, Object modelValue) {
        if (modelValue == null) {
            return "";
        }

        if (modelValue instanceof Goal) {
            return String.valueOf(((Goal) modelValue).getId());
        } else {
            // Throw an exception if we get an unexpected type.
            throw new ConverterException("The value is not a valid Goal instance.");
        }
    }
}


