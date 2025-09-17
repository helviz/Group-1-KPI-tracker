package org.pahappa.systems.client.converters;

import org.pahappa.systems.kpiTracker.core.services.GoalService;
import org.pahappa.systems.kpiTracker.models.goalCreation.Goal;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Converter for Goal entity
 * 
 * @author system
 * 
 */
@FacesConverter("goalConverter")
public class GoalConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            GoalService goalService = ApplicationContextProvider.getBean(GoalService.class);
            return goalService.getInstanceByID(value);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Goal) {
            return ((Goal) object).getId();
        }
        return null;
    }
}
