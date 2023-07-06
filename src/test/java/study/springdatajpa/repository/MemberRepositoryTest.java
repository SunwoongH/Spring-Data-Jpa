package study.springdatajpa.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.springdatajpa.entity.Member;
import study.springdatajpa.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @DisplayName("회원을 저장한다.")
    @Test
    void saveTest() {
        // given
        Member member = createMember("memberA", 25, null);

        // when
        Member savedMember = memberRepository.save(member);

        // then
        Member findMember = memberRepository.findById(savedMember.getId())
                .orElseThrow(() -> new IllegalArgumentException("id: " + savedMember.getId() + " 회원은 존재하지 않습니다."));
        assertThat(findMember.getId()).isEqualTo(savedMember.getId());
        assertThat(findMember.getUsername()).isEqualTo(savedMember.getUsername());
        assertThat(findMember.getAge()).isEqualTo(savedMember.getAge());
        assertThat(findMember.getTeam()).isEqualTo(savedMember.getTeam());
    }

    @DisplayName("회원을 삭제한다.")
    @Test
    void deleteTest() {
        // given
        Member member = createMember("memberA", 25, null);
        Member savedMember = memberRepository.save(member);

        // when
        memberRepository.delete(member);

        // then
        assertThrows(IllegalArgumentException.class, () -> memberRepository.findById(savedMember.getId())
                .orElseThrow(() -> new IllegalArgumentException("id: " + savedMember.getId() + " 회원은 존재하지 않습니다.")));
    }

    @DisplayName("회원들을 조회한다.")
    @Test
    void findAllTest() {
        // given
        final int memberCount = 2;
        Member memberA = createMember("memberA", 25, null);
        Member memberB = createMember("memberB", 25, null);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        // when
        List<Member> members = memberRepository.findAll();

        // then
        assertThat(members.size()).isEqualTo(memberCount);
    }

    private Member createMember(String username, int age, Team team) {
        return Member.builder()
                .username(username)
                .age(age)
                .team(team)
                .build();
    }
}