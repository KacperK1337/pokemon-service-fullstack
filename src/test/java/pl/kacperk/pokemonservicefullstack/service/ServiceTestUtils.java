package pl.kacperk.pokemonservicefullstack.service;

import org.springframework.web.server.ResponseStatusException;

public class ServiceTestUtils {

    protected static final Class<ResponseStatusException> RESPONSE_STATUS_EXC_CLASS =
        ResponseStatusException.class;
    protected static final String STATUS_PROP = "status";

}
