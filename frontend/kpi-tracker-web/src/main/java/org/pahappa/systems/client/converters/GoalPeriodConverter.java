package org.pahappa.systems.client.converters;

import org.pahappa.systems.kpiTracker.core.services.GoalPeriodService;
import org.pahappa.systems.kpiTracker.models.goalMgt.GoalPeriod;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.convert.ConverterException;

@FacesConverter("goalPeriodConverter")
public class GoalPeriodConverter implements Converter {
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String submittedValue) {
        if (submittedValue == null || submittedValue.trim().isEmpty()) {
            return null;
        }
        try {
            GoalPeriodService service = ApplicationContextProvider.getBean(GoalPeriodService.class);
            return service.getInstanceByID(submittedValue);
        } catch (Exception e) {
            throw new ConverterException("Could not convert " + submittedValue + " to a GoalPeriod.", e);
        }
    }
    @Override
    public String getAsString(FacesContext context, UIComponent component, Object modelValue) {
        if (modelValue == null) {
            return "";
        }
        if (modelValue instanceof GoalPeriod) {
            // This part is still correct. We must convert the Long ID to a String here.
            return String.valueOf(((GoalPeriod) modelValue).getId());
        } else {
            throw new ConverterException("The value is not a valid GoalPeriod instance.");
        }
    }
}