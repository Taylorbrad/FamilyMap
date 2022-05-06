package Services;

import java.util.UUID;

public abstract class BaseService {

    String generateUUID()
    {
        final String uuid = UUID.randomUUID().toString().replace("-", "");
        //System.out.println("uuid = " + uuid);
        return uuid.substring(0,10);
    }
}
