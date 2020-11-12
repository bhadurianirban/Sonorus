/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sonorus.ui.terminstance;

import java.io.Serializable;
import java.util.List;
import javax.faces.model.SelectItem;

/**
 *
 * @author bhaduri
 */
public class FormField implements Serializable{
    private String label;
    private Object value;
    private String metaKey;
    private boolean required;
    private boolean disabled;
    private List<SelectItem> selectItems;

    public FormField() {
    }

    public FormField(String label, Object value, String metaKey, boolean required, boolean disabled, List<SelectItem> selectItems) {
        this.label = label;
        this.value = value;
        this.metaKey = metaKey;
        this.required = required;
        this.disabled = disabled;
        this.selectItems = selectItems;
    }

    public String getMetaKey() {
        return metaKey;
    }

    public void setMetaKey(String metaKey) {
        this.metaKey = metaKey;
    }
    
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
    
    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public List<SelectItem> getSelectItems() {
        return selectItems;
    }

    public void setSelectItems(List<SelectItem> selectItems) {
        this.selectItems = selectItems;
    }


    
}
