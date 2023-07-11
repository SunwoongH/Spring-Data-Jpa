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
class MemberJpaRepositoryTest {
    @Autowired
    MemberJpaRepository memberJpaRepository;

    @DisplayName("회원을 저장한다.")
    @Test
    void saveTest() {
        // given
        Member member = createMember("memberA", 25, null);

        // when
        Member savedMember = memberJpaRepository.save(member);

        // then
        Member findMember = memberJpaRepository.findById(savedMember.getId())
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
        Member savedMember = memberJpaRepository.save(member);

        // when
        memberJpaRepository.delete(member);

        // then
        assertThrows(IllegalArgumentException.class, () -> memberJpaRepository.findById(savedMember.getId())
                .orElseThrow(() -> new IllegalArgumentException("id: " + savedMember.getId() + " 회원은 존재하지 않습니다.")));
    }

    @DisplayName("회원들을 조회한다.")
    @Test
    void findAllTest() {
        // given
        final int memberCount = 2;
        Member memberA = createMember("memberA", 25, null);
        Member memberB = createMember("memberB", 25, null);
        memberJpaRepository.save(memberA);
        memberJpaRepository.save(memberB);

        // when
        List<Member> members = memberJpaRepository.findAll();

        // then
        assertThat(members.size()).isEqualTo(memberCount);
    }

    @DisplayName("특정 이름을 가지고 특정 나이보다 많은 회원들을 조회한다.")
    @Test
    void findByUsernameAndAgeGreaterThanTest() {
        // given
        final int maxAge = 25;
        Member memberA = createMember("memberA", 20, null);
        Member memberB = createMember("memberB", 25, null);
        memberJpaRepository.save(memberA);
        memberJpaRepository.save(memberB);

        // when
        List<Member> members = memberJpaRepository.findByUsernameAndAgeGreaterThan("memberA", maxAge);

        // then
        assertThat(members.size()).isEqualTo(0);
    }

    @DisplayName("특정 이름을 가진 회원들을 조회한다.")
    @Test
    void findByUsernameByNamedQueryTest() {
        // given
        final String username = "joy";
        Member memberA = createMember("joy", 25, null);
        Member memberB = createMember("joy", 25, null);
        memberJpaRepository.save(memberA);
        memberJpaRepository.save(memberB);

        // when
        List<Member> members = memberJpaRepository.findByUsername(username);

        // then
        assertThat(members.size()).isEqualTo(2);
        assertThat(members).contains(memberA, memberB);
    }

    @DisplayName("특정 나이인 회원들을 페이징 조회한다.")
    @Test
    void findByPageTest() {
        // given
        final int age = 25;
        final int count = 5;
        for (int i = 0; i < count; i++) {
            Member member = createMember(String.valueOf(i), age, null);
            memberJpaRepository.save(member);
        }

        // when
        final int offset = 0;
        final int limit = 3;
        List<Member> members = memberJpaRepository.findByPage(age, offset, limit);
        long totalCount = memberJpaRepository.totalCount(age);

        // then
        assertThat(members.size()).isEqualTo(limit);
        assertThat(totalCount).isEqualTo(count);
    }

    @DisplayName("특정 나이 이상인 모든 회원들의 나이를 현재 나이 + 1 한다.")
    @Test
    void bulkUpdateTest() {
        // given
        final int age = 25;
        final int count = 5;
        for (int i = 0; i < count; i++) {
            Member member = createMember(String.valueOf(i), age + i, null);
            memberJpaRepository.save(member);
        }

        // when
        int resultCount = memberJpaRepository.bulkAgePlus(25);

        // then
        assertThat(resultCount).isEqualTo(count);
    }

    private Member createMember(String username, int age, Team team) {
        return Member.builder()
                .username(username)
                .age(age)
                .team(team)
                .build();
    }
}