package com.cloudberry.cloudberry.rest.api;

import com.cloudberry.cloudberry.service.api.FluxService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/flux")
@RequiredArgsConstructor
public class FluxDataRest {
    private final FluxService fluxService;

    @PostMapping("/query")
    public String getQueryResult(@RequestBody String rawQuery) {
        return fluxService.doQuery(rawQuery);
    }
}
