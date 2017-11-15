package com.github.lany192.sample;

public interface DomainConfig {
    String BASE_URL = "https://lany192.github.io";

    String APP_GETHUB_DOMAIN = "https://api.github.com";
    String APP_GANK_DOMAIN = "http://gank.io";
    String APP_DOUBAN_DOMAIN = "https://api.douban.com";

    String GITHUB_DOMAIN_NAME = "github";
    String GANK_DOMAIN_NAME = "gank";
    String DOUBAN_DOMAIN_NAME = "douban";


    String DOMAIN_NAME = "Domain-Name";
    String GLOBAL_DOMAIN_NAME = "Global-Domain-Name";
    String DOMAIN_NAME_HEADER = DOMAIN_NAME + ": ";

    String HEADER_1 = DOMAIN_NAME_HEADER + GITHUB_DOMAIN_NAME;
    String HEADER_2 = DOMAIN_NAME_HEADER + GANK_DOMAIN_NAME;
    String HEADER_3 = DOMAIN_NAME_HEADER + DOUBAN_DOMAIN_NAME;
}
