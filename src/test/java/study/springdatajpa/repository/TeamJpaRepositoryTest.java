package study.springdatajpa.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.springdatajpa.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
class TeamJpaRepositoryTest {
    @Autowired
    TeamJpaRepository teamJpaRepository;

    @DisplayName("팀을 저장한다.")
    @Test
    void saveTest() {
        // given
        Team team = createTeam("teamA");

        // when
        Team savedTeam = teamJpaRepository.save(team);

        // then
        Team findTeam = teamJpaRepository.findById(savedTeam.getId())
                .orElseThrow(() -> new IllegalArgumentException("id: " + savedTeam.getId() + " 팀은 존재하지 않습니다."));
        assertThat(findTeam.getId()).isEqualTo(savedTeam.getId());
        assertThat(findTeam.getName()).isEqualTo(savedTeam.getName());
        assertThat(findTeam.getMembers()).isEqualTo(savedTeam.getMembers());
    }

    @DisplayName("팀을 삭제한다.")
    @Test
    void deleteTest() {
        // given
        Team team = createTeam("teamA");
        Team savedTeam = teamJpaRepository.save(team);

        // when
        teamJpaRepository.delete(team);

        // then
        assertThrows(IllegalArgumentException.class, () -> teamJpaRepository.findById(savedTeam.getId())
                .orElseThrow(() -> new IllegalArgumentException("id: " + savedTeam.getId() + " 팀은 존재하지 않습니다.")));
    }

    @DisplayName("팀들을 조회한다.")
    @Test
    void findAllTest() {
        // given
        final int teamCount = 2;
        Team teamA = createTeam("teamA");
        Team teamB = createTeam("teamB");
        teamJpaRepository.save(teamA);
        teamJpaRepository.save(teamB);

        // when
        List<Team> teams = teamJpaRepository.findAll();

        // then
        assertThat(teams.size()).isEqualTo(teamCount);
    }

    private Team createTeam(String name) {
        return Team.builder()
                .name(name)
                .build();
    }
}