package study.till.back.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import study.till.back.dto.post.CreatePostReponse;
import study.till.back.dto.post.FindPostResponse;
import study.till.back.dto.post.PostRequest;
import study.till.back.dto.CommonResponse;
import study.till.back.entity.Member;
import study.till.back.entity.Post;
import study.till.back.exception.member.NotFoundMemberException;
import study.till.back.exception.member.NotMatchMemberException;
import study.till.back.exception.post.NotFoundPostException;
import study.till.back.repository.MemberRepository;
import study.till.back.repository.PostRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    public ResponseEntity<List<FindPostResponse>> findPosts() {
        List<Post> posts = postRepository.findAll();

        List<FindPostResponse> findPostResponses = posts.stream().map(post -> FindPostResponse.builder()
                .id(post.getId())
                .email(post.getMember().getEmail())
                .title(post.getTitle())
                .contents(post.getContents())
                .createdDate(post.getCreatedDate())
                .updatedDate(post.getUpdatedDate())
                .build()
        ).collect(Collectors.toList());
        return ResponseEntity.ok(findPostResponses);
    }

    public ResponseEntity<FindPostResponse> findPost(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);

        if (post == null) throw new NotFoundPostException();

        FindPostResponse findPostResponse = FindPostResponse.builder()
                .id(post.getId())
                .email(post.getMember().getEmail())
                .title(post.getTitle())
                .contents(post.getContents())
                .createdDate(post.getCreatedDate())
                .updatedDate(post.getUpdatedDate())
                .build();

        return ResponseEntity.ok(findPostResponse);
    }

    @Transactional
    public ResponseEntity<CreatePostReponse> createPost(PostRequest postRequest) {
        Member member = memberRepository.findById(postRequest.getEmail()).orElse(null);
        if (member == null) throw new NotFoundMemberException();

        Post post = Post.builder()
                .title(postRequest.getTitle())
                .contents(postRequest.getContents())
                .member(member)
                .build();
        postRepository.save(post);

        long id = post.getId();

        CreatePostReponse createPostReponse = CreatePostReponse.builder()
                .id(id)
                .status("SUCCESS")
                .message("게시글 작성이 완료되었습니다.")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(createPostReponse);
    }

    @Transactional
    public ResponseEntity<CommonResponse> updatePost(Long id, PostRequest postRequest) {
        Member member = memberRepository.findById(postRequest.getEmail()).orElse(null);
        if (member == null) throw new NotFoundMemberException();

        Post post = postRepository.findById(id).orElse(null);
        if (post == null) throw new NotFoundPostException();

        if (!postRequest.getEmail().equals(post.getMember().getEmail())) throw new NotMatchMemberException();

        post.updatePost(postRequest.getTitle(), postRequest.getContents());
        postRepository.save(post);

        CommonResponse commonResponse = CommonResponse.builder()
                .status("SUCCESS")
                .message("게시글이 수정되었습니다.")
                .build();
        return ResponseEntity.ok(commonResponse);
    }
    @Transactional
    public ResponseEntity<CommonResponse> deletePost(Long id) {
        Post post = postRepository.findById(id).orElse(null);
        if (post == null) throw new NotFoundPostException();

        postRepository.deleteById(id);

        CommonResponse commonResponse = CommonResponse.builder()
                .status("SUCCESS")
                .message("게시글이 삭제되었습니다.")
                .build();
        return ResponseEntity.ok(commonResponse);
    }
}
