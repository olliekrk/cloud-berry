package com.cloudberry.cloudberry.rest.api;

import com.cloudberry.cloudberry.service.api.BucketsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/buckets")
public class BucketsRest {
    private final BucketsService bucketsService;

    @GetMapping
    public List<String> getBucketsNames() {
        return bucketsService.getBucketNames();
    }

    @PostMapping("{bucketName}")
    public void createBucket(@PathVariable("bucketName") String bucketName) {
        bucketsService.createBucket(bucketName);
    }

    @DeleteMapping("{bucketName}")
    public void deleteBucket(@PathVariable("bucketName") String bucketName) {
        bucketsService.deleteBucket(bucketName);
    }

}
