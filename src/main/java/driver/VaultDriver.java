package driver;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import crypt.CryptEngine;
import crypt.CryptEngineImpl;
import crypt.CryptUtils;
import crypt.VaultDataKey;
import exceptions.EmptyResponseException;
import exceptions.IncorrectPasswordException;
import vault.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class VaultDriver {
    private String vaultId;
    private String vaultUrl;
    private CryptEngine cryptEngine;
    private SQLiteHandler sqLiteHandler;
    private Entity currentEntity;
    private String dataKey;

    public VaultDriver(){
        this.cryptEngine = new CryptEngineImpl();
        this.vaultId = null;
        this.vaultUrl = null;
        this.sqLiteHandler = null;
        this.currentEntity = null;
    }

    public void initializeNewVault(String password, String url) throws SQLException, NoSuchAlgorithmException, VaultDoesNotExistException {
        this.vaultId = UUID.randomUUID().toString();
        this.vaultUrl = url + "/" + this.vaultId + ".db";
        this.sqLiteHandler = new SQLiteHandler();
        this.cryptEngine.generateKeyFromPassword(password);
        this.dataKey = CryptUtils.generateKey();
        this.sqLiteHandler.initializeNewDatabase(this.vaultId, this.vaultUrl, cryptEngine.getIvAsHex(), cryptEngine.encrypt(this.vaultId), cryptEngine.encrypt(this.dataKey));
    }

    public void connectToVault(String vaultUrl, String password) throws SQLException, IncorrectPasswordException, VaultDoesNotExistException, EmptyResponseException {
        this.vaultUrl = vaultUrl;
        this.sqLiteHandler = new SQLiteHandler();
        this.sqLiteHandler.connectToDatabase(this.vaultUrl);
        this.cryptEngine.regenerateKeyWithPasswordAndIv(password, sqLiteHandler.retrieveIv());
        this.dataKey = this.cryptEngine.decrypt(this.sqLiteHandler.retrieveKey());
        this.vaultId = this.sqLiteHandler.retrieveId();
        if(!this.confirmPassword()) throw new IncorrectPasswordException();
    }

    public void changePassword(String oldPassword, String newPassword) throws SQLException, IncorrectPasswordException, EmptyResponseException {
        CryptEngine tmpCryptEngine = new CryptEngineImpl(oldPassword);
        tmpCryptEngine.regenerateKeyWithPasswordAndIv(oldPassword, this.cryptEngine.getIvAsHex());
        if(this.vaultId.equals(tmpCryptEngine.decrypt(this.sqLiteHandler.retrieveCheckMessage()))){
            CryptEngine cryptEngine = new CryptEngineImpl(newPassword);
            cryptEngine.regenerateKeyWithPasswordAndIv(newPassword, this.cryptEngine.getIvAsHex());
            this.cryptEngine = cryptEngine;
            this.sqLiteHandler.updateConfig(this.cryptEngine.encrypt(this.vaultId), this.cryptEngine.encrypt(this.dataKey));
        }
        else{
            throw new IncorrectPasswordException();
        }
    }

    public Map<Integer, String> listEntities() throws SQLException {
        Map<Integer, String> map = new HashMap<Integer, String>();
        this.sqLiteHandler.getEntityNames().forEach((id, value) -> {
            VaultDataKey vaultDataKey = new VaultDataKey(value.getIv(), this.dataKey);
            try {
                map.put(id, vaultDataKey.decrypt(value.getResponse()));
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            }
        });
        return map;
    }

    public void createNewEntity(String name, Map<String, EntityField> values) throws SQLException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException {
        this.currentEntity = new Entity(name);
        this.currentEntity.setFields(values);
        Gson gson = new Gson();
        VaultDataKey vaultDataKey = new VaultDataKey(this.dataKey);
        this.sqLiteHandler.insertNewEntity(vaultDataKey.getIvAsString(), vaultDataKey.encrypt(gson.toJson(this.currentEntity.getName())), vaultDataKey.encrypt(gson.toJson(this.currentEntity.getFields())));
    }

    public void createNewEntity(Entity entity) throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException, SQLException {
        this.currentEntity = entity;
        Gson gson = new Gson();
        VaultDataKey vaultDataKey = new VaultDataKey(this.dataKey);
        this.sqLiteHandler.insertNewEntity(vaultDataKey.getIvAsString(), vaultDataKey.encrypt(gson.toJson(this.currentEntity.getName())), vaultDataKey.encrypt(gson.toJson(this.currentEntity.getFields())));
    }

    public void updateEntity(int id, String name, Map<String, EntityField> values) throws SQLException, EmptyResponseException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException {
        this.loadEntity(id);
        VaultDataKey vaultDataKey = new VaultDataKey(this.sqLiteHandler.getEntityIv(id), this.dataKey);
        this.currentEntity.setName(name);
        this.currentEntity.setFields(values);
        Gson gson = new Gson();
        this.sqLiteHandler.updateEntityName(id, vaultDataKey.encrypt(gson.toJson(this.currentEntity.getName())));
        this.sqLiteHandler.updateEntityData(id, vaultDataKey.encrypt(gson.toJson(this.currentEntity.getFields())));
    }

    public void saveUpdatedEntity() throws SQLException, EmptyResponseException, NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        VaultDataKey vaultDataKey = new VaultDataKey(this.sqLiteHandler.getEntityIv(this.currentEntity.getId()), this.dataKey);
        Gson gson = new Gson();
        this.sqLiteHandler.updateEntityName(this.currentEntity.getId(), vaultDataKey.encrypt(gson.toJson(this.currentEntity.getName())));
        this.sqLiteHandler.updateEntityData(this.currentEntity.getId(), vaultDataKey.encrypt(gson.toJson(this.currentEntity.getFields())));
    }

    public void deleteEntity(int id) throws SQLException, EmptyResponseException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        this.loadEntity(id);
        this.sqLiteHandler.deleteEntity(id);
    }

    public void loadEntity(int id) throws SQLException, EmptyResponseException, NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        Gson gson = new Gson();
//        VaultDataKey vaultDataKey = new VaultDataKey(this.cryptEngine.decrypt(this.sqLiteHandler.getEntityIv(id)), this.dataKey);
        VaultDataKey vaultDataKey = new VaultDataKey(this.sqLiteHandler.getEntityIv(id), this.dataKey);
        String plainName = vaultDataKey.decrypt(this.sqLiteHandler.getEntityName(id));
        String plainData = vaultDataKey.decrypt(this.sqLiteHandler.getEntityData(id));
        this.currentEntity = new Entity(gson.fromJson(plainName, String.class));

        Map tmpMap =  gson.fromJson(plainData, LinkedHashMap.class);

        Map<String, EntityField> tmpFieldsMap = new LinkedHashMap();

        tmpMap.forEach((k, v) -> {
            EntityField tmpEntityField;
            Map tmpFieldMap = (LinkedTreeMap) v;
            String tmpFieldContent = tmpFieldMap.get("fieldContent").toString();
            String tmpFieldType = tmpFieldMap.get("fieldType").toString();
            if(tmpFieldType.equals("PASSWORD")){
                tmpEntityField = new EntityField(tmpFieldContent, VaultDataFieldType.PASSWORD);
            }else{
                tmpEntityField = new EntityField(tmpFieldContent, VaultDataFieldType.TEXT);
            }
            tmpFieldsMap.put(k.toString(), tmpEntityField);
        });
        //this.currentEntity.setFields(gson.fromJson(plainData, LinkedHashMap.class));
        this.currentEntity.setFields(tmpFieldsMap);
        this.currentEntity.setId(id);
    }

    private boolean confirmPassword() throws SQLException, EmptyResponseException {
        if(this.vaultId.equals(this.cryptEngine.decrypt(this.sqLiteHandler.retrieveCheckMessage()))) return true;
        return false;
    }

    public Entity getCurrentEntity() {
        return currentEntity;
    }
}
