package com.cloudberry.cloudberry.rest.api;

import com.cloudberry.cloudberry.service.api.BucketsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public void createBucket(@PathVariable String bucketName) {
        bucketsService.createBucketIfNotExists(bucketName);
    }

    @DeleteMapping("{bucketName}")
    public void deleteBucket(@PathVariable String bucketName) {
        bucketsService.deleteBucket(bucketName);
    }

}
