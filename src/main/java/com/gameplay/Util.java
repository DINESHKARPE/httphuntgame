package com.gameplay;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.json.JSONException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Util {

    public static String decryptString(final String encryptedMessage, int key) {
        encryptedMessage.toLowerCase();
        StringBuffer decryptMessage = new StringBuffer();
        for (int i = 0; i < encryptedMessage.length(); i++) {
            char alphabet = encryptedMessage.charAt(i);

            if (alphabet >= 'A' && alphabet <= 'Z') {
                alphabet = (char) (alphabet - key);

                if (alphabet < 'A') {
                    alphabet = (char) (alphabet - 'A' + 'Z' + 1);
                }
                decryptMessage.append(alphabet);
            } else {
                decryptMessage.append(alphabet);
            }
        }
        return decryptMessage.toString();
    }

    public static JsonArray findToolInHiddenTools(final String hiddenTools, JsonArray toolArray) {

        for (int i = 0; i < toolArray.size(); i++) {
            String toolString = toolArray.get(i).getAsString();
            int staringChar = 0;
            for (int j = 0; j < toolString.length(); j++) {
                staringChar = hiddenTools.indexOf(toolString.charAt(j), staringChar);
                if (staringChar == -1) {
                    toolArray.remove(i);
                    break;
                } else {
                    staringChar += 1;
                }
            }
        }
        return toolArray;
    }

    public static JsonArray findToolUsage(JsonArray toolArray) throws ParseException, JSONException {

        HashMap<String,Integer> hashMap = new HashMap<>();
        for (int i = 0; i < toolArray.size(); i++) {
            JsonObject toolString = toolArray.get(i).getAsJsonObject();

            String useStartTime = toolString.get("useStartTime").getAsString();
            String useEndTime = toolString.get("useEndTime").getAsString();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date useStartDateTime = simpleDateFormat.parse(useStartTime);
            Date useEndDateTime = simpleDateFormat.parse(useEndTime);

            long elapsed = useEndDateTime.getTime() - useStartDateTime.getTime();
            long min = elapsed / 60;

            if(hashMap.containsKey(toolString.get("name").getAsString())){
                int exitingValue = hashMap.get(toolString.get("name").getAsString());
                hashMap.put(toolString.get("name").getAsString(),exitingValue+(int)(min/1000));
            }else {
                hashMap.put(toolString.get("name").getAsString(), (int) (min/1000));

            }

        }

        LinkedHashMap<String, Integer> reverseSortedMap = new LinkedHashMap<>();
        hashMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));

        Set<Map.Entry<String, Integer>> tempData = reverseSortedMap.entrySet();

        JsonArray sortData = new JsonArray();
        tempData.forEach(stringIntegerEntry -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name",stringIntegerEntry.getKey());
            jsonObject.addProperty("timeUsedInMinutes",stringIntegerEntry.getValue());
            sortData.add(jsonObject);
        });

        return sortData;
    }

    public static JsonArray findToolsToTakeSorted(JsonArray jsonArray, int maximumWeight){

        List<Integer> weightArray = new ArrayList<>();
        Map<Integer,String> toolNameWeight= new HashMap<>();

        for(int i=0;i<jsonArray.size();i++){

            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();

            weightArray.add(jsonObject.get("weight").getAsInt());
            toolNameWeight.put(jsonObject.get("weight").getAsInt(),jsonObject.get("name").getAsString());
        }

        Collections.sort(weightArray);


        int maxWeight = weightArray.get(weightArray.size() - 1);

        int totalMax;

        JsonArray toolCarryArray = new JsonArray();

        if(maxWeight == maximumWeight){
            toolCarryArray.add(toolNameWeight.get(maxWeight));
            toolNameWeight.remove(maxWeight);
            return toolCarryArray;
        }else {
            toolCarryArray.add(toolNameWeight.get(maxWeight));
            toolNameWeight.remove(maxWeight);

            for(int i = weightArray.size()-2;i > 0;i--){

               totalMax  = weightArray.get(i) + maxWeight;

               if(totalMax == maximumWeight){
                   toolCarryArray.add(toolNameWeight.get(weightArray.get(i)));
                   toolNameWeight.remove(maxWeight);
                   break;
               }else {
                   totalMax =  totalMax - weightArray.get(i);
                   continue;
               }
            }
            return toolCarryArray;
        }

    }
}
