package org.zkoss.ganttz.data;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Date;
import java.util.List;

/**
 * This class contains the information of a task. It can be modified and
 * notifies of the changes to the interested parties. <br/>
 * @author Óscar González Fernández <ogonzalez@igalia.com>
 */
public abstract class Task implements ITaskFundamentalProperties {

    private PropertyChangeSupport fundamentalPropertiesListeners = new PropertyChangeSupport(
            this);

    private PropertyChangeSupport visibilityProperties = new PropertyChangeSupport(
            this);

    private ITaskFundamentalProperties fundamentalProperties;

    private boolean visible = true;

    public Task(ITaskFundamentalProperties fundamentalProperties) {
        this.fundamentalProperties = fundamentalProperties;
    }

    public Task() {
        this(new DefaultFundamentalProperties());
    }

    public Task(String name, Date beginDate, long lengthMilliseconds) {
        this();
        if (name == null)
            throw new IllegalArgumentException("name cannot be null");
        if (beginDate == null)
            throw new IllegalArgumentException("beginDate cannot be null");
        if (lengthMilliseconds < 0)
            throw new IllegalArgumentException(
                    "length in milliseconds must be positive. Instead it is "
                            + lengthMilliseconds);
        this.fundamentalProperties.setName(name);
        this.fundamentalProperties.setBeginDate(beginDate);
        this.fundamentalProperties.setLengthMilliseconds(lengthMilliseconds);
    }

    public abstract boolean isLeaf();

    public boolean isContainer() {
        return !isLeaf();
    }

    public abstract boolean isExpanded() throws UnsupportedOperationException;

    public abstract List<Task> getTasks()
            throws UnsupportedOperationException;

    public boolean isVisible() {
        return visible;
    }

    protected void setVisible(boolean visible) {
        boolean previousValue = this.visible;
        this.visible = visible;
        visibilityProperties.firePropertyChange("visible", previousValue,
                this.visible);
    }

    public String getName() {
        return fundamentalProperties.getName();
    }

    public void setName(String name) {
        String previousValue = fundamentalProperties.getName();
        fundamentalProperties.setName(name);
        fundamentalPropertiesListeners.firePropertyChange("name",
                previousValue, name);
    }

    public void setBeginDate(Date beginDate) {
        Date previousValue = fundamentalProperties.getBeginDate();
        fundamentalProperties.setBeginDate(beginDate);
        fundamentalPropertiesListeners.firePropertyChange("beginDate",
                previousValue, fundamentalProperties.getBeginDate());
    }

    public Date getBeginDate() {
        return new Date(fundamentalProperties.getBeginDate().getTime());
    }

    public void setLengthMilliseconds(long lengthMilliseconds) {
        long previousValue = fundamentalProperties.getLengthMilliseconds();
        fundamentalProperties.setLengthMilliseconds(lengthMilliseconds);
        fundamentalPropertiesListeners.firePropertyChange("lengthMilliseconds",
                previousValue, fundamentalProperties.getLengthMilliseconds());
    }

    public long getLengthMilliseconds() {
        return fundamentalProperties.getLengthMilliseconds();
    }

    public void addVisibilityPropertiesChangeListener(
            PropertyChangeListener listener) {
        this.visibilityProperties.addPropertyChangeListener(listener);
    }

    public void addFundamentalPropertiesChangeListener(
            PropertyChangeListener listener) {
        this.fundamentalPropertiesListeners.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.fundamentalPropertiesListeners
                .removePropertyChangeListener(listener);
    }

    public Date getEndDate() {
        return new Date(getBeginDate().getTime() + getLengthMilliseconds());
    }

    public String getNotes() {
        return fundamentalProperties.getNotes();
    }

    public void setNotes(String notes) {
        String previousValue = fundamentalProperties.getNotes();
        this.fundamentalProperties.setNotes(notes);
        fundamentalPropertiesListeners.firePropertyChange("notes",
                previousValue, this.fundamentalProperties.getNotes());
    }

    public void setEndDate(Date value) {
        setLengthMilliseconds(value.getTime() - getBeginDate().getTime());
    }

    public void removed() {
        setVisible(false);
    }

}