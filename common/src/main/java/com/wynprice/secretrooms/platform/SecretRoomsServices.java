package com.wynprice.secretrooms.platform;

import com.wynprice.secretrooms.SecretRooms7;
import com.wynprice.secretrooms.platform.services.ISecretRoomsPlatformHelper;

import java.util.ServiceLoader;

public class SecretRoomsServices {

    public static final ISecretRoomsPlatformHelper PLATFORM = load(ISecretRoomsPlatformHelper.class);

    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        SecretRooms7.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}