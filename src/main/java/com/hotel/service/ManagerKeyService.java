package main.java.com.hotel.service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ManagerKeyService {
    private static final Set<String> managerKeys = new HashSet<>();

    public static String generateManagerKey() {
        String key = UUID.randomUUID().toString();
        managerKeys.add(key);
        return key;
    }

    public static boolean validateManagerKey(String key) {
        return managerKeys.contains(key);
    }
}
