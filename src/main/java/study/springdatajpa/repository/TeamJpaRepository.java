package study.springdatajpa.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import study.springdatajpa.entity.Team;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class TeamJpaRepository {
    private final EntityManager entityManager;

    public Team save(Team team) {
        entityManager.persist(team);
        return team;
    }

    public void delete(Team team) {
        entityManager.remove(team);
    }

    public Optional<Team> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Team.class, id));
    }

    public List<Team> findAll() {
        return entityManager.createQuery("select t from Team t", Team.class)
                .getResultList();
    }

    public long count() {
        return entityManager.createQuery("select count(t) from Team t", Long.class)
                .getSingleResult();
    }
}
