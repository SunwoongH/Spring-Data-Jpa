package study.springdatajpa.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class MemberAndTeamTest {
    @PersistenceContext
    EntityManager entityManager;

    @DisplayName("회원과 팀 사이의 연관관계가 매핑된다.")
    @Test
    void associationBetweenMembersAndTeams() {
        // given
        Team teamA = createTeam("teamA");
        entityManager.persist(teamA);
        Team teamB = createTeam("teamB");
        entityManager.persist(teamB);
        final int pivot = 2;
        final int memberCount = 4;
        for (int i = 0; i < memberCount; i++) {
            Member member = createMember(String.valueOf(i), (i + 1) * 3, (i < pivot) ? teamA : teamB);
            entityManager.persist(member);
        }
        entityManager.flush();
        entityManager.clear();

        // when
        List<Member> members = entityManager.createQuery("select m from Member m", Member.class)
                .getResultList();

        // then
        for (Member member : members) {
            if (Integer.parseInt(member.getUsername()) < pivot) {
                assertThat(member.getTeam().getId()).isEqualTo(teamA.getId());
            } else {
                assertThat(member.getTeam().getId()).isEqualTo(teamB.getId());
            }
        }
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