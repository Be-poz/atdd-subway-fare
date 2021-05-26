package wooteco.subway.auth.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.auth.dto.TokenRequest;
import wooteco.subway.auth.dto.TokenResponse;
import wooteco.subway.auth.infrastructure.JwtTokenProvider;
import wooteco.subway.member.dao.MemberDao;
import wooteco.subway.member.domain.Member;

@Service
@Transactional
public class AuthService {
    private MemberDao memberDao;
    private JwtTokenProvider jwtTokenProvider;

    public AuthService(MemberDao memberDao, JwtTokenProvider jwtTokenProvider) {
        this.memberDao = memberDao;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public TokenResponse login(TokenRequest request) {
        Long id = getIdWhenValidLogin(request);
        String token = jwtTokenProvider.createToken(id);
        return new TokenResponse(token);
    }

    private Long getIdWhenValidLogin(TokenRequest request) {
        Member member = memberDao.findByEmail(request.getEmail());
        member.checkPassword(request.getPassword());
        return member.getId();
    }

    public Member findMemberByToken(String credentials) {
        jwtTokenProvider.validateToken(credentials);
        Long id = jwtTokenProvider.getPayload(credentials);
        return memberDao.findById(id);
    }
}
