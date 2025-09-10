package org.pahappa.systems.client.converters;

import org.pahappa.systems.kpiTracker.core.services.OrganisationGoalService;
import org.pahappa.systems.kpiTracker.models.goalMgt.OrganisationGoal;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.convert.ConverterException;

@FacesConverter("organisationGoalConverter")
public class OrganisationGoalConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String submittedValue) {
        if (submittedValue == null || submittedValue.trim().isEmpty()) {
            return null; // Return null if no value provided
        }

        try {
            OrganisationGoalService service = ApplicationContextProvider.getBean(OrganisationGoalService.class);
            if (service == null) {
                throw new ConverterException("OrganisationGoalService is not available");
            }

            OrganisationGoal goal = service.getInstanceByID(submittedValue);
            if (goal == null) {
                throw new ConverterException("Organisation goal with ID " + submittedValue + " not found");
            }

            return goal;
        } catch (Exception e) {
            throw new ConverterException(
                    "Could not convert " + submittedValue + " to an OrganisationGoal: " + e.getMessage(), e
            );
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object modelValue) {
        if (modelValue == null) {
            return ""; // Return empty string for null values
        }

        if (modelValue instanceof OrganisationGoal) {
            return String.valueOf(((OrganisationGoal) modelValue).getId());
        } else {
            throw new ConverterException(
                    "The value is not a valid OrganisationGoal instance. Got: " + modelValue.getClass().getName()
            );
        }
    }
}
