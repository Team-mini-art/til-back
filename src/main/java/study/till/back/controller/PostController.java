package study.till.back.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import study.till.back.dto.FindPostResponse;
import study.till.back.dto.PostRequest;
import study.till.back.dto.CommonResponse;
import study.till.back.dto.UpdatePostRequest;
import study.till.back.service.PostService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController {

    private final PostService postService;

    @GetMapping("/posts")
    public ResponseEntity<List<FindPostResponse>> findPosts() {
        return postService.findPosts();
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<FindPostResponse> findPost(@PathVariable("id") Long id) {
        return postService.findPost(id);
    }

    @PostMapping("/posts")
    public ResponseEntity<CommonResponse> createPost(@RequestBody PostRequest postRequest) {
        return postService.createPost(postRequest);
    }

    @PutMapping("/posts")
    public ResponseEntity<CommonResponse> updatePost(@RequestBody UpdatePostRequest postRequest) {
        return postService.updatePost(postRequest);
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<CommonResponse> deletePost(@PathVariable("id") Long id) {
        return postService.deletePost(id);
    }
}
