package com.cloudberry.cloudberry.rest.api;

import com.cloudberry.cloudberry.service.api.BucketsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/buckets")
public class BucketsRest {
    private final BucketsService bucketsService;

    @DeleteMapping("{bucketName}")
    public void deleteBucket(@PathVariable("bucketName") String bucketName) {
        bucketsService.deleteBucket(bucketName);
    }
}
