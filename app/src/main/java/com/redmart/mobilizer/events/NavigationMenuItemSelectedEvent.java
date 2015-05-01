package com.redmart.mobilizer.events;

import com.redmart.mobilizer.models.Project;

/**
 * Created by Joshua on 26/3/15.
 */
public class NavigationMenuItemSelectedEvent {
    private int position;
    private Project project;

    public NavigationMenuItemSelectedEvent(int position, Project project) {
        this.position = position;
        this.project = project;
    }

    public int getPosition() {
        return position;
    }

    public Project getProject() {
        return project;
    }
}
