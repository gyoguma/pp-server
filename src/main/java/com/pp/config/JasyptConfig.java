package com.pp.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JasyptConfig {

    @Bean("jasyptStringEncryptor")
    public StringEncryptor stringEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(System.getProperty("jasypt.encryptor.password"));
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setPoolSize(1);
        encryptor.setConfig(config);
        return encryptor;
    }
}

//@Configuration
//public class JasyptConfig {
//
//    @Value("${jasypt.encryptor.password}")
//    private String encryptKey;
//
//    @Bean(name = "jasyptEncryptor")
//    public SimpleStringPBEConfig encryptor() {
//        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
//        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
//        config.setPassword(encryptKey); // 암호화할 때 사용하는 키
//        config.setAlgorithm("PBEWithMD5AndDES"); // 암호화 알고리즘 default
//        config.setKeyObtentionIterations("1000"); // 반복할 해싱 회수 default
//        config.setPoolSize("1"); // 인스턴스 pool default
//        config.setProviderName("SunJCE"); // default
//        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator"); // salt 생성 클래스 default
//        config.setStringOutputType("base64"); //인코딩 방식
//        encryptor.setConfig(config);
//        return config;
//    }
//}