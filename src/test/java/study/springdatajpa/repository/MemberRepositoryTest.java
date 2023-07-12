package study.springdatajpa.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import study.springdatajpa.dto.MemberDto;
import study.springdatajpa.entity.Member;
import study.springdatajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;
    @PersistenceContext
    EntityManager entityManager;

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

    @DisplayName("특정 이름을 가지고 특정 나이보다 많은 회원을 조회한다.")
    @Test
    void findByUsernameAndAgeGreaterThanTest() {
        // given
        final int maxAge = 25;
        Member memberA = createMember("memberA", 20, null);
        Member memberB = createMember("memberB", 25, null);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        // when
        List<Member> members = memberRepository.findByUsernameAndAgeGreaterThan("memberA", maxAge);

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
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        // when
        List<Member> members = memberRepository.findByUsername(username);

        // then
        assertThat(members.size()).isEqualTo(2);
        assertThat(members).contains(memberA, memberB);
    }

    @DisplayName("특정 이름을 가지고 특정 나이인 회원들을 조회한다.")
    @Test
    void findMembersByMethodQueryTest() {
        // given
        final String username = "joy";
        final int age = 25;
        Member memberA = createMember("joy", 20, null);
        Member memberB = createMember("joy", 25, null);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        // when
        List<Member> members = memberRepository.findMembers(username, age);

        // then
        assertThat(members.size()).isEqualTo(1);
        assertThat(members).contains(memberB);
    }

    @DisplayName("회원들의 이름을 조회한다.")
    @Test
    void findUsernamesTest() {
        // given
        final String usernameA = "memberA";
        final String usernameB = "memberB";
        Member memberA = createMember(usernameA, 20, null);
        Member memberB = createMember(usernameB, 25, null);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        // when
        List<String> usernames = memberRepository.findUsernames();

        // then
        assertThat(usernames.size()).isEqualTo(2);
        assertThat(usernames).contains(usernameA, usernameB);
    }

    @DisplayName("회원 정보들을 dto로 직접 조회한다.")
    @Test
    void findMemberDtosTest() {
        // given
        Team team = createTeam("teamA");
        Member memberA = createMember("memberA", 20, team);
        Member memberB = createMember("memberB", 25, team);
        teamRepository.save(team);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        // when
        List<MemberDto> memberDtos = memberRepository.findMemberDtos();

        // then
        assertThat(memberDtos.size()).isEqualTo(2);
        assertThat(memberDtos.get(0)).isInstanceOf(MemberDto.class);
    }

    @DisplayName("특정 이름들을 가진 회원들을 조회한다.")
    @Test
    void findByUsernamesTest() {
        // given
        final String usernameA = "memberA";
        final String usernameB = "memberB";
        final List<String> usernames = List.of(usernameA, usernameB);
        Member memberA = createMember(usernameA, 25, null);
        Member memberB = createMember(usernameB, 25, null);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        // when
        List<Member> members = memberRepository.findByUsernames(usernames);

        // then
        assertThat(members.size()).isEqualTo(2);
        assertThat(members).contains(memberA, memberB);
    }

    @DisplayName("List<Member>, Member, Optional<Member>을 반환한다.")
    @Test
    void returnTypeTest() {
        // given
        final String username = "joy";
        Member member = createMember(username, 20, null);
        memberRepository.save(member);

        // when
        List<Member> members = memberRepository.findMemberListByUsername(username);
        Member findMember = memberRepository.findMemberByUsername(username);
        Optional<Member> findOptionalMember = memberRepository.findOptionalMemberByUsername(username);

        // then
        assertThat(members).isInstanceOf(List.class);
        assertThat(findMember).isInstanceOf(Member.class);
        assertThat(findOptionalMember).isInstanceOf(Optional.class);
    }

    @DisplayName("특정 나이인 회원들을 페이징 조회한다.")
    @Test
    void findByPageTest() {
        // given
        final int age = 25;
        final long count = 7;
        for (int i = 0; i < count; i++) {
            Member member = createMember(String.valueOf(i), age, null);
            memberRepository.save(member);
        }

        // when
        final int offset = 0;
        final int limit = 3;
        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "username"));
        Page<Member> memberPage = memberRepository.findByAge(age, pageRequest);
        List<Member> members = memberPage.getContent();

        // then
        assertThat(members.size()).isEqualTo(limit);
        assertThat(memberPage.getTotalElements()).isEqualTo(count);
        assertThat(memberPage.getNumber()).isEqualTo(0);
        assertThat(memberPage.getTotalPages()).isEqualTo((count % limit) == 0 ? count / limit : (count / limit + 1));
        assertThat(memberPage.isFirst()).isTrue();
        assertThat(memberPage.hasNext()).isEqualTo((count != limit) ? true : false);
    }

    @DisplayName("특정 나이 이상인 모든 회원들의 나이를 현재 나이 + 1 한다.")
    @Test
    void bulkUpdateTest() {
        // given
        final int age = 25;
        final int count = 5;
        for (int i = 0; i < count; i++) {
            Member member = createMember(String.valueOf(0), age + i, null);
            memberRepository.save(member);
        }

        // when
        int resultCount = memberRepository.bulkAgePlus(25);

        // then
        assertThat(resultCount).isEqualTo(count);
    }

    @DisplayName("회원을 조회할 때 팀도 같이 조회한다.")
    @Test
    void findMembersWithTeam() {
        // give
        Team teamA = createTeam("teamA");
        Team teamB = createTeam("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member memberA = createMember("memberA", 25, teamA);
        Member memberB = createMember("memberB", 25, teamB);
        memberRepository.save(memberA);
        memberRepository.save(memberB);
        entityManager.flush();
        entityManager.clear();

        // when
        List<Member> members = memberRepository.findMembersWithTeam();
        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }
    }

    @DisplayName("쿼리 힌트가 적용된다.")
    @Test
    void queryHintTest() {
        // given
        Member member = createMember("joy", 25, null);
        memberRepository.save(member);
        entityManager.flush();
        entityManager.clear();

        // when
        Member findMember = memberRepository.findReadOnlyByUsername("joy");
        findMember.setUsername("joy2");
        entityManager.flush();
    }

    @DisplayName("락이 적용된다.")
    @Test
    void lockTest() {
        // given
        Member member = createMember("joy", 25, null);
        memberRepository.save(member);
        entityManager.flush();
        entityManager.clear();

        // when
        Member findMember = memberRepository.findLockByUsername("joy");
    }

    @DisplayName("생성일 & 생성자 & 수정일 & 수정자를 확인한다.")
    @Test
    void eventBaseEntityTest() throws InterruptedException {
        // given
        Member member = createMember("memberA", 25, null);
        memberRepository.save(member);
        Thread.sleep(100);
        member.setUsername("memberB");
        entityManager.flush();
        entityManager.clear();

        // when
        Member findMember = memberRepository.findById(member.getId())
                .orElseThrow(() -> new IllegalArgumentException("id: " + member.getId() + " 회원은 존재하지 않습니다."));

        // then
        System.out.println("findMember.getCreateDate() = " + findMember.getCreateDate());
        System.out.println("findMember.getLastModifiedDate() = " + findMember.getLastModifiedDate());
        System.out.println("findMember.getCreateBy() = " + findMember.getCreateBy());
        System.out.println("findMember.getLastModifiedBy() = " + findMember.getLastModifiedBy());
    }

    private Team createTeam(String name) {
        return Team.builder()
                .name(name)
                .build();
    }

    private Member createMember(String username, int age, Team team) {
        return Member.builder()
                .username(username)
                .age(age)
                .team(team)
                .build();
    }
}