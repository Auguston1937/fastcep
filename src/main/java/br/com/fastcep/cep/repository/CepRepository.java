package br.com.fastcep.cep.repository;

import br.com.fastcep.cep.entity.Cep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CepRepository extends JpaRepository<Cep, Long>{
}
