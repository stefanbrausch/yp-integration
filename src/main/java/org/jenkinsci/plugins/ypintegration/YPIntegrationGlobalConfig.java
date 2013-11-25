package org.jenkinsci.plugins.ypintegration;

import hudson.Extension;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

@Extension
public class YPIntegrationGlobalConfig extends GlobalConfiguration {

    private String ypLink;
    private String peopleLink;
    private String ypUser;
    private String ypPassword;

    public YPIntegrationGlobalConfig() {
        load();
    }

    @DataBoundConstructor
    public YPIntegrationGlobalConfig(String ypLink, String peopleLink, String ypUser, String ypPassword) {
        this.ypLink = ypLink;
        this.peopleLink = peopleLink;
        this.ypUser = ypUser;
        this.ypPassword = ypPassword;
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
        ypLink = formData.getString("ypLink").trim();
        peopleLink = formData.getString("peopleLink").trim();
        ypUser = formData.getString("ypUser");
        ypPassword = formData.getString("ypPassword");
        save();
        return true;
    }
    
    public static YPIntegrationGlobalConfig get() {
        return GlobalConfiguration.all().get(
                YPIntegrationGlobalConfig.class);
    }

    public String getYpLink() {
        return ypLink == null ? Secrets.YP_LINK : ypLink;
    }

    public String getPeopleLink() {
        return peopleLink == null ? Secrets.PEOPLE_LINK : peopleLink;
    }

    public String getYpUser() {
        return ypUser == null ? Secrets.YP_USER : ypLink;
    }

    public String getYpPassword() {
        return ypPassword == null ? Secrets.YP_PASSWORD : ypLink;
        
    }

}
