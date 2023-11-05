package study.till.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.till.back.entity.MemberAttach;

public interface MemberAttachRepository extends JpaRepository<MemberAttach, Long> {
    long countByMember_EmailAndContentTypeContaining(String email, String contentType);

    void deleteByMember_Email(String email);
}
