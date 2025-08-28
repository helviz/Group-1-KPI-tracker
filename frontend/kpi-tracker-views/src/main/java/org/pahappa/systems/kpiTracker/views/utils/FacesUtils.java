package org.pahappa.systems.kpiTracker.views.utils;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.PrimeFaces;

public class FacesUtils {

    public static void addInfo(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail));
    }

    public static void addError(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail));
    }

    /**
     * Executes a JavaScript script in the browser.
     * @param script The JavaScript to execute.
     */
    public static void execute(String script) {
        PrimeFaces.current().executeScript(script);
    }
}