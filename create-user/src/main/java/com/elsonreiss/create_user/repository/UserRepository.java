package com.elsonreiss.create_user.repository;

import com.elsonreiss.create_user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * REPOSITÓRIO DE USUÁRIOS
 *
 * Interface que estende JpaRepository do Spring Data JPA.
 * O Spring Data JPA cria automaticamente a implementação desta interface em tempo de execução.
 *
 * JpaRepository<User, Long> significa:
 * - User: A entidade que este repositório gerencia
 * - Long: O tipo do ID da entidade (campo @Id na classe User)
 *
 * MÉTODOS HERDADOS AUTOMATICAMENTE:
 * - save(): salvar ou atualizar
 * - findById(): buscar por ID
 * - findAll(): buscar todos
 * - deleteById(): deletar por ID
 * - existsById(): verificar se existe
 * - count(): contar registros
 * E muitos outros...
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * QUERY METHOD - Método de consulta customizado
     *
     * O Spring Data JPA cria a query SQL automaticamente baseado no nome do método!
     *
     * Padrão: findBy + NomeDoCampo
     * "findByEmail" vira: SELECT * FROM tb_users WHERE email = ?
     *
     * Optional<User> é usado porque o email pode não existir no banco.
     * Optional evita NullPointerException e força você a tratar o caso de "não encontrado".
     *
     * @param email - O email do usuário a ser buscado
     * @return Optional contendo o User se encontrado, ou vazio se não encontrado
     */
    Optional<User> findByEmail(String email);
}
