package org.pahappa.systems.client.converters;

import org.pahappa.systems.kpiTracker.core.services.GoalLevelService;
import org.pahappa.systems.kpiTracker.models.goalMgt.GoalLevel;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.convert.ConverterException;

@FacesConverter("goalLevelConverter")
public class GoalLevelConverter implements Converter {

    /**
     * Converts the submitted String ID from the form back into a full GoalLevel object.
     */
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String submittedValue) {
        if (submittedValue == null || submittedValue.trim().isEmpty()) {
            return null;
        }

        try {
            GoalLevelService service = ApplicationContextProvider.getBean(GoalLevelService.class);
            return service.getInstanceByID(submittedValue);

        } catch (Exception e) {
            // Added error handling for robustness
            throw new ConverterException("Could not convert '" + submittedValue + "' to a GoalLevel.", e);
        }
    }

    /**
     * Converts a GoalLevel object into its unique String ID for the HTML dropdown.
     */
    @Override
    public String getAsString(FacesContext context, UIComponent component, Object modelValue) {
        if (modelValue == null) {
            return ""; // Return an empty string for null, which is safer.
        }

        if (modelValue instanceof GoalLevel) {
             return String.valueOf(((GoalLevel) modelValue).getId());
        } else {
            throw new ConverterException("The value is not a valid GoalLevel instance.");
        }
    }
}