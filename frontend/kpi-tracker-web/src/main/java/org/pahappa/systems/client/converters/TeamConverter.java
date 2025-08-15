package org.pahappa.systems.client.converters;

import org.pahappa.systems.kpiTracker.core.services.TeamService;
import org.pahappa.systems.kpiTracker.models.team.Team;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("teamConverter")
public class TeamConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return ApplicationContextProvider.getBean(TeamService.class).getInstanceByID(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null || value instanceof String) {
            return null;
        }
        return ((Team) value).getId();
    }
}