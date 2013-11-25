package org.jenkinsci.plugins.ypintegration;

import java.io.IOException;

import javax.servlet.ServletException;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import hudson.model.Action;
import hudson.model.AbstractProject;

public class YPIntegrationJobAction implements Action {

    private final AbstractProject<?, ?> project;
    private YPData ypData;

    public YPIntegrationJobAction(AbstractProject<?, ?> project) {
        this.project = project;
        final YPIntegrationJobProperty myJobProp = YPIntegrationJobProperty.get(project);
        if (myJobProp != null) {
            this.ypData = myJobProp.getYpData();
        }
    }

    public String getIconFileName() {
        return "/plugin/yp-integration/img/yp-icon.png";
    }

    public String getDisplayName() {
        return "1and1 YP Integration";
    }

    public String getUrlName() {
        return "ypintegration";
    }

    public AbstractProject<?, ?> getProject() {
        return project;
    }

    public String getYPID() {

        return YPIntegrationJobProperty.get(project) == null ? null : YPIntegrationJobProperty.get(project).getId();
    }

    public YPData getYpData() {

        return this.ypData;

    }

    public String getPeopleLink() {
        final YPIntegrationGlobalConfig config = YPIntegrationGlobalConfig.get();
        return config.getPeopleLink() + ypData.getOwnerId();
    }

    public void doFetchAllYPData(StaplerRequest req, StaplerResponse rsp) throws ServletException, IOException {

        if (getYPID() != null) {
            final FetchDataFromYP ypFetcher = new FetchDataFromYP();
            this.ypData = ypFetcher.fetchAllYPData(getYPID());
        }
        rsp.sendRedirect("");
    }

    public void doSaveDataToJob(StaplerRequest req, StaplerResponse rsp) throws ServletException, IOException {
        YPIntegrationJobProperty.get(project).setAndSaveYpData(this.ypData);
        rsp.sendRedirect("");
    }

    public boolean isNameNewer() {
        return !(ypData.getName().equals(YPIntegrationJobProperty.get(project).getYpData().getName()));
    }

    public boolean isOwnerNewer() {
        return !((ypData.getOwnerName().equals(YPIntegrationJobProperty.get(project).getYpData().getOwnerName())) && ypData
                .getOwnerId().equals(YPIntegrationJobProperty.get(project).getYpData().getOwnerId()));
    }

    public boolean isDescriptionNewer() {
        return !(ypData.getDescription().equals(YPIntegrationJobProperty.get(project).getYpData().getDescription()));
    }

    public boolean isDocLinkNewer() {
        return !(ypData.getDocLink().equals(YPIntegrationJobProperty.get(project).getYpData().getDocLink()));
    }

    public boolean isIssueLinkNewer() {
        return !(ypData.getIssueLink().equals(YPIntegrationJobProperty.get(project).getYpData().getIssueLink()));
    }

    public boolean isScmLinkNewer() {
        return !(ypData.getScmLink().equals(YPIntegrationJobProperty.get(project).getYpData().getScmLink()));
    }

    public boolean isCiLinkNewer() {
        return !(ypData.getCiLink().equals(YPIntegrationJobProperty.get(project).getYpData().getCiLink()));
    }

    public boolean hasDifferentScmLink() {
        return YPIntegrationJobProperty.get(project).hasDifferentScmLink(ypData.getScmLink());

    }

    public boolean hasDifferentCiLink() {
        return !(ypData.getCiLink().equals(project.getAbsoluteUrl()));
    }

    public boolean hasAnyChanges() {
        return isNameNewer() || isOwnerNewer() || isDescriptionNewer() || isDocLinkNewer() || isIssueLinkNewer()
                || isScmLinkNewer() || isCiLinkNewer();

    }
}
