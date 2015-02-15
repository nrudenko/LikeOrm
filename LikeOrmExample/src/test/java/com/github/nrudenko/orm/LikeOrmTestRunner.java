package com.github.nrudenko.orm;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;

import java.util.Properties;

public class LikeOrmTestRunner extends RobolectricTestRunner {

    public LikeOrmTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected Properties getConfigProperties() {
        Properties configProperties = super.getConfigProperties();
        if (configProperties == null) {
            configProperties = new Properties();
        }
        configProperties.put("manifest", "src/main/AndroidManifest.xml");
        configProperties.put("emulateSdk", "16");
        return configProperties;
    }
}
