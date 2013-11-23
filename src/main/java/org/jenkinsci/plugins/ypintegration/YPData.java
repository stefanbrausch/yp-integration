package org.jenkinsci.plugins.ypintegration;

public class YPData {

    private String id;

    private String description;

    private String ownerName;
    
    private String ownerId;

    private String scmLink;

    private String docLink;

    private String issueLink;

    private String permaLink;

    private String ciLink;

    private String name;

    public YPData() {
        this.id = null;
        initData();

    }

    public YPData(String id) {
        this.id = id;
        initData();
    }

    private void initData() {
        this.name = Constants.NOT_FETCHED;
        this.ownerName = Constants.NOT_FETCHED;
        this.ownerId = Constants.NOT_FETCHED;
        this.description = Constants.NOT_FETCHED;
        this.docLink = Constants.NOT_FETCHED;
        this.issueLink = Constants.NOT_FETCHED;
        this.permaLink = Constants.NOT_FETCHED;
        this.scmLink = Constants.NOT_FETCHED;
        this.ciLink = Constants.NOT_FETCHED;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
    
    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getScmLink() {
        return scmLink;
    }

    public void setScmLink(String scmLink) {
        this.scmLink = scmLink;
    }

    public String getDocLink() {
        return docLink;
    }

    public void setDocLink(String docLink) {
        this.docLink = docLink;
    }

    public String getIssueLink() {
        return issueLink;
    }

    public void setIssueLink(String issueLink) {
        this.issueLink = issueLink;
    }

    public String getPermaLink() {
        return permaLink;
    }

    public void setPermaLink(String permaLink) {
        this.permaLink = permaLink;
    }

    public String getCiLink() {
        return ciLink;
    }

    public void setCiLink(String ciLink) {
        this.ciLink = ciLink;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
