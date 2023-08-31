package com.akagiyui.drive.service.impl;

import com.akagiyui.drive.entity.KeyValueConfig;
import com.akagiyui.drive.repository.ConfigRepository;
import com.akagiyui.drive.service.ConfigService;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * 配置表操作 实现类
 *
 * @author AkagiYui
 */
@Service
public class ConfigServiceImpl implements ConfigService {

    @Resource
    private ConfigRepository configRepository;

    /**
     * 通过配置项键查找配置项值
     *
     * @param key 配置项键
     * @return 配置项值
     */
    private Optional<String> findByKey(String key) {
        return configRepository.findByConfigKey(key).map(KeyValueConfig::getConfigValue);
    }

    /**
     * 通过配置项键查找配置项值，并转换为布尔值
     *
     * @param key                  配置项键
     * @param defaultValueSupplier 默认值生产者
     * @return 配置项值
     */
    private Boolean findBoolean(String key, Supplier<Boolean> defaultValueSupplier) {
        return findByKey(key).map(Boolean::parseBoolean).orElseGet(defaultValueSupplier);
    }

    /**
     * 保存配置项
     *
     * @param key   配置项键
     * @param value 配置项值
     */
    private void save(String key, boolean value) {
        KeyValueConfig keyValueConfig = new KeyValueConfig().setConfigKey(key).setConfigValue("" + value);
        configRepository.save(keyValueConfig);
    }

    @Override
    @Cacheable(value = "config", key = "T(com.akagiyui.drive.service.ConfigService).REGISTER_ENABLED")
    public boolean isRegisterEnabled() {
        return findBoolean(REGISTER_ENABLED, () -> setRegisterEnabled(true));
    }

    @Override
    @CachePut(value = "config", key = "T(com.akagiyui.drive.service.ConfigService).REGISTER_ENABLED")
    public boolean setRegisterEnabled(boolean enabled) {
        save(REGISTER_ENABLED, enabled);
        return enabled;
    }

    @Override
    @Cacheable(value = "config", key = "T(com.akagiyui.drive.service.ConfigService).IS_INITIALIZED")
    public boolean isInitialized() {
        return findBoolean(IS_INITIALIZED, () -> setInitialized(false));
    }

    @Override
    @CachePut(value = "config", key = "T(com.akagiyui.drive.service.ConfigService).IS_INITIALIZED")
    public boolean setInitialized(boolean initialized) {
        save(IS_INITIALIZED, initialized);
        return initialized;
    }
}