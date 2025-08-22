package org.pahappa.systems.client.converters;

import org.pahappa.systems.kpiTracker.core.services.DepartmentService;
import org.pahappa.systems.kpiTracker.models.department.Department;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.convert.ConverterException;

@FacesConverter("departmentConverter")
public class DepartmentConverter implements Converter {

    /**
     * Converts the submitted String ID from the form back into a full Department object.
     * This is correct for your service layer, which expects a String.
     */
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String submittedValue) {
        if (submittedValue == null || submittedValue.trim().isEmpty()) {
            return null;
        }

        try {
            DepartmentService service = ApplicationContextProvider.getBean(DepartmentService.class);

            // CORRECT: We pass the String directly, as your service expects.
            return service.getDepartmentById(submittedValue);

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

        if (modelValue instanceof Department) {
            // THE FIX: The Long ID from the object MUST be converted to a String before returning.
            // This fixes the compilation error.
            return String.valueOf(((Department) modelValue).getId());
        } else {
            // Throw an exception if we get an unexpected type.
            throw new ConverterException("The value is not a valid Department instance.");
        }
    }
}