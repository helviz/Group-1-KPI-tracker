package org.pahappa.systems.client.converters;

import org.pahappa.systems.kpiTracker.core.services.StaffService;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Converter for Staff entity
 * 
 * @author system
 * 
 */
@FacesConverter("staffConverter")
public class StaffConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            System.out.println("StaffConverter: getAsObject called with null/empty value");
            return null;
        }
        try {
            System.out.println("StaffConverter: getAsObject called with value: " + value);
            StaffService staffService = ApplicationContextProvider.getBean(StaffService.class);
            if (staffService == null) {
                System.err.println("StaffConverter: StaffService is null");
                return null;
            }
            Staff staff = staffService.getInstanceByID(value);
            System.out.println("StaffConverter: Found staff: " + (staff != null ? staff.getFullName() : "null"));
            return staff;
        } catch (Exception e) {
            System.err.println("StaffConverter: Error getting staff by ID '" + value + "': " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object object) {
        if (object == null) {
            System.out.println("StaffConverter: getAsString called with null object");
            return null;
        }
        if (object instanceof Staff) {
            String id = ((Staff) object).getId();
            System.out.println("StaffConverter: getAsString returning ID: " + id + " for staff: "
                    + ((Staff) object).getFullName());
            return id;
        }
        System.out.println("StaffConverter: getAsString called with non-Staff object: " + object.getClass().getName());
        return null;
    }
}
