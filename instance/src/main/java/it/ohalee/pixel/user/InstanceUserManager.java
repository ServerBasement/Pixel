package it.ohalee.pixel.user;

import java.util.UUID;

public class InstanceUserManager extends AbstractUserManager<UUID, UUID, User> {

    public InstanceUserManager() {
    }

    @Override
    protected User createUser(UUID uniqueId) {
        return new User(uniqueId);
    }

}
