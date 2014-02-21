package org.kevoree.brain.neuralcomponent;

import java.util.Objects;

/**
 * User: assaad.moawad
 * Date: 2/20/14
 * Time: ${Time}
 * University of Luxembourg - Snt
 * assaad.mouawad@gmail.com
 */
public class CommunicationMessage {
    private String componentName;
    private Object value;

    public void setComponentName(String componentName)
    {
        this.componentName=componentName;
    }
    public void setValue(Object value)
    {
        this.value=value;
    }

    public String getComponentName()
    {
        return componentName;
    }
    public Object getValue(){
        return value;
    }


}
