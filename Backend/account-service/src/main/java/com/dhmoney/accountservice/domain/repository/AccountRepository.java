package com.dhmoney.accountservice.domain.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.dhmoney.accountservice.domain.model.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    //FindByAlias
    @Query("SELECT b FROM Account b WHERE b.alias = ?1")
    Account findByAlias(String alias);

    //FindByCVU
    @Query("SELECT b FROM Account b WHERE b.cvu = ?1")
    Account findByCVU(String cvu);

    //FindByUser
    @Query("SELECT b FROM Account b WHERE b.userId = ?1")
    Account findByUser(Long user);
}
