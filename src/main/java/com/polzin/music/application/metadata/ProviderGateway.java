package com.polzin.music.application.metadata;

import java.util.Optional;

public interface ProviderGateway<T> {

    String providerName();

    Optional<T> findByExternalId(String externalId);
}
