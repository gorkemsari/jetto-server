package org.jetto.server.parser;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.jetto.server.model.Model;

/**
 *
 * @author gorkemsari - jetto.org
 */
public class Parser {
    private final Gson gson;

    public Parser() {
        gson = new Gson();
    }

    public int getType(String json){
        try{
            return gson.fromJson(json, Model.class).getType();
        }
        catch (JsonSyntaxException e){
            return -1;
        }
    }

    public <T> T toModel(String json, Class<T> type){
        try{
            return gson.fromJson(json, type);
        }
        catch (JsonSyntaxException e){
            return null;
        }
    }

    public String toJson(Model model){
        try{
            return gson.toJson(model);
        }
        catch (Exception e){
            return null;
        }
    }
}