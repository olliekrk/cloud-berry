package com.cloudberry.cloudberry.rest.api;

import com.cloudberry.cloudberry.service.api.FluxService;
import com.influxdb.query.FluxTable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/flux")
@RequiredArgsConstructor
public class FluxRest {
    private final FluxService fluxService;

    @PostMapping("/query")
    public List<FluxTable> getQueryResult(@RequestBody String rawQuery) {
        return fluxService.doQuery(rawQuery);
    }
}
