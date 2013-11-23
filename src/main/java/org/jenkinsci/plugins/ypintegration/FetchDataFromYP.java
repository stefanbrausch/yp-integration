package org.jenkinsci.plugins.ypintegration;

import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class FetchDataFromYP {

    public YPData fetchAllYPData(String id) {
        final YPData fetchedData = new YPData(id);
        YpEngine yp;
        JSONObject json;
        JSONArray jsonArray;
        yp = YpEngine.connect();
        try {
            jsonArray = yp.getJsonArray(String.format("software/%s/responsibles", id));

            fetchedData.setOwnerName(String.valueOf(((JSONObject) jsonArray.get(1)).get("displayName")));
            fetchedData.setOwnerId(String.valueOf(((JSONObject) jsonArray.get(1)).get("personId")));

            json = yp.getJson(String.format("software/%s", id));

            fetchedData.setName(String.valueOf(json.get("name")));
            fetchedData.setDescription(String.valueOf(json.get("description")));
            fetchedData.setPermaLink(String.valueOf(json.get("permLink")).replace("\"", "")
                    .replace("\\", "").replace("[", "").replace("]", ""));
            fetchedData.setCiLink(checkfornull((String.valueOf(((JSONObject) json.get("uris")).get("HUDSON")))).replace("\"", "")
                    .replace("\\", "").replace("[", "").replace("]", ""));
            fetchedData.setScmLink(checkfornull(String.valueOf(((JSONObject) json.get("uris")).get("SVN"))).replace("\"", "")
                    .replace("\\", "").replace("[", "").replace("]", ""));
            fetchedData.setIssueLink(checkfornull(String.valueOf(((JSONObject) json.get("uris")).get("JIRA"))).replace("\"", "")
                    .replace("\\", "").replace("[", "").replace("]", ""));
            fetchedData.setDocLink(checkfornull(String.valueOf(((JSONObject) json.get("uris")).get("DOCUMENTATION")))
                    .replace("\"", "").replace("\\", "").replace("[", "").replace("]", ""));

        } catch (IOException e) {
            Constants.LOGGER.info("Error occurred while YP Data fetching: " + e.getMessage());
            fetchedData.setName(Constants.NOT_FOUND);
            fetchedData.setOwnerName(Constants.NOT_FOUND);
            fetchedData.setOwnerId(Constants.NOT_FOUND);
            fetchedData.setCiLink(Constants.NOT_FOUND);
            fetchedData.setDocLink(Constants.NOT_FOUND);
            fetchedData.setIssueLink(Constants.NOT_FOUND);
            fetchedData.setPermaLink(Constants.NOT_FOUND);
            fetchedData.setScmLink(Constants.NOT_FOUND);
        } finally {
            yp.disconnect();
        }
        return fetchedData;

    }

    private String checkfornull(String value) {
        if (("null").equals(value)) {
            return Constants.NOT_SET;
        }

        return value;
    }

}
