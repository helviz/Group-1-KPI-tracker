package org.pahappa.systems.client.converters;

import org.pahappa.systems.kpiTracker.core.services.TeamService;
import org.pahappa.systems.kpiTracker.models.team.Team;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter("teamConverter")
public class TeamConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            TeamService service = ApplicationContextProvider.getBean(TeamService.class);
            if (service == null) {
                throw new ConverterException("TeamService is not available");
            }
            Team team = service.getInstanceByID(value);
            if (team == null) {
                throw new ConverterException("Team with ID " + value + " not found");
            }
            return team;
        } catch (Exception e) {
            throw new ConverterException(
                    "Could not convert '" + value + "' to a Team: " + e.getMessage(), e);
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof Team) {
            return String.valueOf(((Team) value).getId());
        } else {
            throw new ConverterException(
                    "The value is not a valid Team instance. Got: " + value.getClass().getName());
        }
    }
}