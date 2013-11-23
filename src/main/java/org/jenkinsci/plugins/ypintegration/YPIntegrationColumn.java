package org.jenkinsci.plugins.ypintegration;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Job;
import hudson.views.ListViewColumnDescriptor;
import hudson.views.ListViewColumn;

public class YPIntegrationColumn extends ListViewColumn {

    @DataBoundConstructor
    public YPIntegrationColumn() {
        super();
    }

    public String getYPID(Job<?, ?> job) {
        String ypId = Constants.NOT_SET;
        if (!(job instanceof AbstractProject<?, ?>)) {
            return "Not an AbstractProject";
        }
        final AbstractProject<?, ?> project = (AbstractProject<?, ?>) job;
        try {
            ypId = project.getProperty(YPIntegrationJobProperty.class).getId();
        } catch (NullPointerException e) {
            // values could not be retrieved
            return Constants.NOT_SET;
        }
        return ypId;
    }

    @Extension
    public static class DescriptorImpl extends ListViewColumnDescriptor {

        @Override
        public boolean shownByDefault() {
            return false;
        }

        @Override
        public String getDisplayName() {
            return "YP ID";
        }
    }
}