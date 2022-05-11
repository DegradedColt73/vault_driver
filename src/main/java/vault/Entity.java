package vault;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Entity {
    private int id;
    private String name;
    private Map<String, EntityField> fields;

    public Entity(String name){
        this.name = name;
    }

    public void addField(String fieldName){
        this.fields.put(fieldName, null);
    }

    public String getFieldContentByName(String fieldName){
        return this.fields.get(fieldName).getFieldContent();
    }

    public void deleteField(String fieldName){
        this.fields.remove(fieldName);
    }

    public List<String> getFieldNames(){
        List<String> keysList = new ArrayList<String>();
        for (String key : this.fields.keySet()){
            keysList.add(key);
        }
        return keysList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, EntityField> getFields() {
        return fields;
    }

    public void setFields(Map<String, EntityField> fields) {
        this.fields = fields;
    }
}
