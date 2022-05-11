package vault;

import java.util.UUID;

public class VaultConfig {
    private String id;
    private String salt;

    public String getSalt() {
        return salt;
    }

    public String getId() {
        return id;
    }

    public VaultConfig(String salt){
        this.id = UUID.randomUUID().toString();
        this.salt = salt;
    }
}
