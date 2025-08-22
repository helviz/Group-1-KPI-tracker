package org.pahappa.systems.kpiTracker.views.OrganisationFit;

import org.pahappa.systems.kpiTracker.core.services.SurveyCategoryService;
import org.pahappa.systems.kpiTracker.models.organisationFit.SurveyCategory;
import org.pahappa.systems.kpiTracker.security.HyperLinks;
import org.pahappa.systems.kpiTracker.views.dialogs.DialogForm;
import org.sers.webutils.client.views.presenters.ViewPath;
import org.sers.webutils.server.core.utils.ApplicationContextProvider;


import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(
        name ="surveyCategoryFormDialog", eager = true
)
@ViewPath(path= HyperLinks.SURVEY_CATEGORY_FORM_DIALOG)
@SessionScoped
public class SurveyCategoryFormDialog extends DialogForm<SurveyCategory> {

    private static final long serialVersionUID = 1L;
    private SurveyCategoryService categoryService;
    private boolean edit;

    @PostConstruct
    public void init(){
        this.categoryService = ApplicationContextProvider.getBean(SurveyCategoryService.class);
        if (super.model == null) {
            super.model = new SurveyCategory();
        }
    }

    public SurveyCategoryFormDialog() {
        super(HyperLinks.SURVEY_CATEGORY_FORM_DIALOG, 700, 600);
    }

    public SurveyCategoryFormDialog(String name, int width, int height) {
        super(name, width, height);
    }

    @Override
    public void persist() throws Exception {
        this.categoryService.saveInstance(super.model);
    }

    @Override
    public void resetModal(){
        super.resetModal();
        super.model = new SurveyCategory();
        setEdit(false);
    }

    @Override
    public void setFormProperties() {
        super.setFormProperties();
        if(super.model != null && super.model.getId() != null){
            setEdit(true);
        } else {
            if(super.model == null){
                super.model = new SurveyCategory();
            }
            setEdit(false);
        }
    }

//    setters and getters

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }


    public SurveyCategory getModel() {
        return super.model;
    }


}
