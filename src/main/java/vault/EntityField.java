package vault;

public class EntityField {
    private String fieldContent;
    private VaultDataFieldType fieldType;

    public EntityField(String fieldContent, VaultDataFieldType fieldType){
        this.fieldContent = fieldContent;
        this.fieldType = fieldType;
    }

    public String getFieldContent() {
        return fieldContent;
    }

    public void setFieldContent(String fieldContent) {
        this.fieldContent = fieldContent;
    }

    public VaultDataFieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(VaultDataFieldType fieldType) {
        this.fieldType = fieldType;
    }
}


