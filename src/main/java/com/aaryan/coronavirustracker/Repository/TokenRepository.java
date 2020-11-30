package com.aaryan.coronavirustracker.Repository;

import com.aaryan.coronavirustracker.Domain.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token,Long> {


}
