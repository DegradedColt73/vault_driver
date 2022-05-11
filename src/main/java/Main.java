import driver.VaultDriver;
import exceptions.EmptyResponseException;
import exceptions.IncorrectPasswordException;
import vault.Entity;
import vault.EntityField;
import vault.VaultDataFieldType;
import vault.VaultDoesNotExistException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class Main {

    public static void main(String [] args) throws VaultDoesNotExistException, NoSuchAlgorithmException, SQLException, EmptyResponseException, IncorrectPasswordException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException {
//        VaultDriver vaultDriver = new VaultDriver();
//        vaultDriver.initializeNewVault("password");
//        Map<String, EntityField> map = new LinkedHashMap<String, EntityField>();
//        map.put("login", new EntityField("Mieszko", VaultDataFieldType.TEXT));
//        map.put("hasło", new EntityField("Hasło", VaultDataFieldType.PASSWORD));
//        vaultDriver.createNewEntity("Microsoft", map);
//        map.put("hasło", new EntityField("innehasło", VaultDataFieldType.PASSWORD));
//        vaultDriver.createNewEntity("Google", map);
//        map.put("hasło", new EntityField("jeszczeinnehasło", VaultDataFieldType.PASSWORD));
//        vaultDriver.createNewEntity("Facebook", map);




//        VaultDriver vaultDriver = new VaultDriver();
//        vaultDriver.connectToVault("E:\\Repozytoria\\Vault\\Apps\\vault_driver\\356b16c5-adeb-45e3-b1bf-b99385ce3228.db", "password");
//
//
//        vaultDriver.loadEntity(2);
//        vaultDriver.getCurrentEntity().getFields().put("hasło", new EntityField("HASLOOOO2", VaultDataFieldType.PASSWORD));
//        vaultDriver.getCurrentEntity().getFields().remove("password");
//        vaultDriver.saveUpdatedEntity();
//
//        vaultDriver.getCurrentEntity().getFields().forEach((v, k) -> {
//            System.out.println("NAME: " + v);
//            System.out.println("CONTENT: " + k.getFieldContent());
//        });
//
//        vaultDriver.loadEntity(1);
//        System.out.println(vaultDriver.getCurrentEntity().getFields());
//        vaultDriver.loadEntity(2);
//        System.out.println(vaultDriver.getCurrentEntity().getFields());
//        vaultDriver.loadEntity(3);
//        System.out.println(vaultDriver.getCurrentEntity().getFields());

//        VaultDriver vaultDriver = new VaultDriver();
//        vaultDriver.connectToVault("D:\\OneDrive\\Desktop\\305fa72d-6c9d-432e-a861-2c03fe78c23d.db", "test");
//        vaultDriver.loadEntity(1);
//        Entity entity = vaultDriver.getCurrentEntity();
//        System.out.println("test");

//        VaultDriver vaultDriver = new VaultDriver();
//        vaultDriver.initializeNewVault("test", "E:\\Repozytoria");
//        Map<String, EntityField> map = new LinkedHashMap<String, EntityField>();
//        map.put("login", new EntityField("Mieszko", VaultDataFieldType.TEXT));
//        map.put("hasło", new EntityField("Hasło", VaultDataFieldType.PASSWORD));
//        vaultDriver.createNewEntity("Microsoft", map);
//        map.put("hasło", new EntityField("innehasło", VaultDataFieldType.PASSWORD));
//        vaultDriver.createNewEntity("Google", map);
//        map.put("hasło", new EntityField("jeszczeinnehasło", VaultDataFieldType.PASSWORD));
//        vaultDriver.createNewEntity("Facebook", map);

        VaultDriver vaultDriver = new VaultDriver();
        vaultDriver.connectToVault("E:\\Repozytoria\\Vault\\Apps\\vault_server\\db.db", "password");
        vaultDriver.listEntities();
    }
}
