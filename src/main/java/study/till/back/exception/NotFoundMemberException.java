package study.till.back.exception;

import study.till.back.dto.exception.ErrorCode;


public class NotFoundMemberException extends RuntimeException {
    public NotFoundMemberException() {
        super("DB에 저장된 회원정보를 찾을 수 없습니다.");
    }
}
