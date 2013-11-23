package org.jenkinsci.plugins.ypintegration;

import java.util.ArrayList;
import java.util.Collection;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.TransientProjectActionFactory;
import hudson.model.AbstractProject;

@Extension
public class YPIntegrationActionFactory extends TransientProjectActionFactory {
   
    @Override
    public Collection<? extends Action> createFor(@SuppressWarnings("rawtypes") AbstractProject target) {
        final ArrayList<Action> actions = new ArrayList<Action>();
        final YPIntegrationJobAction newAction = new YPIntegrationJobAction(target);
        actions.add(newAction);
        return actions;
    }
}
