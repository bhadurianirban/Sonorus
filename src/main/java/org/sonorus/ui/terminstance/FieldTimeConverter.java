/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sonorus.ui.terminstance;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author bhaduri
 */
@FacesConverter("fieldTimeConverter")
public class FieldTimeConverter implements Converter {
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String newValue) {
//        Date formFieldDate;
//        try { 
//            formFieldDate =new SimpleDateFormat("dd-MM-yyyy").parse(newValue);
//        } catch (ParseException ex) {
//            Logger.getLogger(FieldDateConverter.class.getName()).log(Level.SEVERE, null, ex);
//            return null;
//        }
        return newValue;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return "";
        }
        String formFieldDate = ((String) value);
//        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
//        String formFieldDateToString = df.format(formFieldDate);
        return formFieldDate;
    }
    
}
