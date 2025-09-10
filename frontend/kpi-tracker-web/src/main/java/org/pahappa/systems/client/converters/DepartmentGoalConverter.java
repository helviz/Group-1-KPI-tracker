package org.pahappa.systems.client.converters;

import org.pahappa.systems.kpiTracker.core.services.DepartmentGoalService;
import org.pahappa.systems.kpiTracker.models.goalMgt.DepartmentGoal;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter("departmentGoalConverter")
public class DepartmentGoalConverter implements Converter {
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            DepartmentGoalService service = ApplicationContextProvider.getBean(DepartmentGoalService.class);
            if (service == null) {
                throw new ConverterException("DepartmentGoalService is not available");
            }
            DepartmentGoal goal = service.getInstanceByID(value);
            if (goal == null) {
                throw new ConverterException("Department goal with ID " + value);
            }
            return goal;

        } catch (Exception e) {
            throw new ConverterException(
                    "Could not convert " + value + "to a Department Goal" + e.getMessage(), e);
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object modelValue) {
        if (modelValue == null) {
            return "";
        }
        if (modelValue instanceof DepartmentGoal) {
            return String.valueOf(((DepartmentGoal) modelValue).getId());
        } else {
            throw new ConverterException(
                    "The value is not a valid DepartmentGoal instance . Got : " + modelValue.getClass().getName());
        }
    }
}
