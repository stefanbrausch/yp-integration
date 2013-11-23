package org.jenkinsci.plugins.ypintegration;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import hudson.model.AbstractProject;
import hudson.model.Descriptor.FormException;
import hudson.model.Job;
import hudson.plugins.git.GitSCM;
import hudson.scm.SCM;
import hudson.scm.SubversionSCM;
import hudson.scm.SubversionSCM.ModuleLocation;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONObject;

import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

public class YPIntegrationJobProperty extends JobProperty<AbstractProject<?, ?>> {

    private YPData ypData = new YPData();
    private String id;

    @DataBoundConstructor
    public YPIntegrationJobProperty(String id, YPData ypData) {
        if (ypData != null)
           this.ypData = ypData;
        this.ypData.setId(id);
        this.id = id;

    }

    public static YPIntegrationJobProperty get(AbstractProject<?, ?> project) {
        if (project == null) {
            return null;
        }
        Job<?, ?> job;

        job = project.getRootProject();

        return job.getProperty(YPIntegrationJobProperty.class);
    }

    public String getId() {
        return ypData.getId();
    }

    public YPData getYpData() {
        return ypData;
    }

    public String getPeopleLink() {
        return Secrets.PEOPLE_LINK + ypData.getOwnerId();
    }
    
    @Override
    public JobProperty<?> reconfigure(StaplerRequest req, JSONObject form) throws Descriptor.FormException {
        String formId = (req.bindJSON(YPIntegrationJobProperty.class, form)).getId();
        if (id.equals(formId))
            return new YPIntegrationJobProperty(id, ypData);
        else
            return new YPIntegrationJobProperty(formId, new YPData());
    }

    @Extension
    public static final class DescriptorImpl extends JobPropertyDescriptor {

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            
            return super.configure(req, json);
        }

        public DescriptorImpl() {
            super(YPIntegrationJobProperty.class);
            load();
        }

        public boolean isApplicable(Class<? extends Job> jobType) {
            return AbstractProject.class.isAssignableFrom(jobType);
        }

        public String getDisplayName() {
            return "1and1 YP Integration";
        }

        //@Override
        //public YPIntegrationJobProperty newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            
        //    return req.bindJSON(YPIntegrationJobProperty.class, formData);
        //}
        
        @Override
        public JobProperty<?> newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            YPIntegrationJobProperty tpp = req.bindJSON(YPIntegrationJobProperty.class, formData);
            if (tpp.getId() == null) {
                tpp = null; // not configured
            }
            return tpp;
        }

    }

    public void setAndSaveYpData(YPData newYpData) {
        this.ypData = newYpData;
        
        try {
            
            this.owner.getRootProject().save();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public boolean hasDifferentScmLink(String scmLink) {
        
        return !(checkSCMPath(this.owner.getScm(), scmLink));
    }

    public boolean hasDifferentScmLink() {

        return hasDifferentScmLink(ypData.getScmLink());

    }

    public boolean hasDifferentCiLink() {
        boolean res = true;

        if (ypData.getCiLink().equals(this.owner.getAbsoluteUrl())) {
            res = false;
        }
        return res;
    }

    private boolean checkSCMPath(SCM scm, String path) {
        Boolean found = false;

        final String[] scmPath = getSCMPath(scm);
        if (scmPath != null) {
            for (int i = 0; i < scmPath.length; i++) {
                final String checkPath = addSlashIfMissing(scmPath[i]);
                // LOGGER.fine("check " + path + " against " + checkPath);
                if ((checkPath.length() > 0)
                        && ((checkPath.length() <= path.length() && checkPath.equalsIgnoreCase(path.substring(0,
                                checkPath.length()))) || ((checkPath.length() > path.length() && path.equalsIgnoreCase(checkPath
                                .substring(0, path.length())))))) {
                    found = true;
                    // LOGGER.fine("Job found!");
                }
            }
        }

        return found;
    }

    @SuppressWarnings("rawtypes")
    private String[] getSCMPath(SCM scm) {

        String[] scmPath = null;

        if (scm instanceof SubversionSCM) {
            final SubversionSCM svn = (SubversionSCM) scm;
            final ModuleLocation[] locs = svn.getLocations();
            scmPath = new String[locs.length];
            for (int i = 0; i < locs.length; i++) {
                final ModuleLocation moduleLocation = locs[i];
                scmPath[i] = moduleLocation.remote;
                // LOGGER.fine(scmPath[i] + " added");
            }
        } else if (scm instanceof GitSCM) {
            final GitSCM git = (GitSCM) scm;
            final List<RemoteConfig> repoList = git.getRepositories();

            scmPath = new String[repoList.size()];
            for (int i = 0; i < repoList.size(); i++) {
                final List<URIish> uris = repoList.get(i).getURIs();
                for (final Iterator iterator = uris.iterator(); iterator.hasNext();) {
                    final URIish urIish = (URIish) iterator.next();
                    scmPath[i] = urIish.toString();
                    // LOGGER.fine(scmPath[i] + " added");
                }
            }
        }
        return scmPath;
    }

    private String addSlashIfMissing(String path) {
        if (!path.endsWith("/")) {
            return path + "/";
        } else {
            return path;
        }
    }

}
