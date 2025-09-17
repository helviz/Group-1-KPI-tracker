package org.pahappa.systems.client.converters;

import org.pahappa.systems.kpiTracker.constants.GoalLevel;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Converter for GoalLevel enum
 * 
 * @author system
 * 
 */
@FacesConverter("goalLevelConverter")
public class GoalLevelConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return GoalLevel.valueOf(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof GoalLevel) {
            return ((GoalLevel) object).name();
        }
        return null;
    }
}
