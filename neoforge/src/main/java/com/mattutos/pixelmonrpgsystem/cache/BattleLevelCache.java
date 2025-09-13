package com.mattutos.pixelmonrpgsystem.cache;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// BattleLevelCache.java
public final class BattleLevelCache {
    public static final Map<java.util.UUID, Integer> originalLevels = new java.util.concurrent.ConcurrentHashMap<>();
    public static final Map<java.util.UUID, Integer> originalExperiences = new java.util.concurrent.ConcurrentHashMap<>();
    public static final Map<java.util.UUID, Boolean> originalDoesLevel = new java.util.concurrent.ConcurrentHashMap<>();
}
