package at.oegeg.etd.Repositories;


import at.oegeg.etd.Entities.TokenBlackList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITokenBlackListRepository extends JpaRepository<TokenBlackList,Long>
{
    boolean existsByToken(String token);
}
