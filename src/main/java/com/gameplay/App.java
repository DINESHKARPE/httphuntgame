package com.gameplay;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;

public class App {
    public static void main(String[] s) {


        try {
            RestClinet restClinet = new RestClinet();


            JsonObject jsonObject = restClinet.getData(Constants.URI_CHALLENGE);

            switch (jsonObject.get("stage").getAsString()) {
                case "1/4":
                    JsonObject firstStepObject = restClinet.getData(Constants.CHALLENGE_INPUT_URI);
                    String decryptedString = Util.decryptString(firstStepObject.get("encryptedMessage").getAsString(), firstStepObject.get("key").getAsInt());
                    restClinet.postData(Constants.CHALLENGE_OUTPUT_URI, "message",decryptedString);
                    break;
                case "2/4":
                    JsonObject secondStepObject = restClinet.getData(Constants.CHALLENGE_INPUT_URI);
                    JsonArray toolsFound = Util.findToolInHiddenTools(secondStepObject.get("hiddenTools").getAsString(), secondStepObject.getAsJsonArray("tools"));
                    restClinet.postData(Constants.CHALLENGE_OUTPUT_URI, "toolsFound",toolsFound.toString());
                    break;
                case "3/4":
                    JsonObject thirdStepObject = restClinet.getData(Constants.CHALLENGE_INPUT_URI);
                    JsonArray toolsSortedOnUsage = Util.findToolUsage(thirdStepObject.getAsJsonArray("toolUsage"));
                    restClinet.postData(Constants.CHALLENGE_OUTPUT_URI, "toolsSortedOnUsage", toolsSortedOnUsage.toString());
                    break;
                case "4/4":
                    JsonObject fourStepObject = restClinet.getData(Constants.CHALLENGE_INPUT_URI);
                    JsonArray toolsToTakeSorted = Util.findToolsToTakeSorted(fourStepObject.getAsJsonArray("tools"), fourStepObject.get("maximumWeight").getAsInt());
                    restClinet.postData(Constants.CHALLENGE_OUTPUT_URI, "toolsToTakeSorted", toolsToTakeSorted.toString());
                    break;

                default:
                    System.out.println("Nothing");


            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


}
