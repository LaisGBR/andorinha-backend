package repository;

import java.util.Calendar;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import model.Comentario;
import model.seletor.ComentarioSeletor;

@Stateless
public class ComentarioRepository extends AbstractCrudRepository {

	public void inserir(Comentario comentario) {
		comentario.setData(Calendar.getInstance());
		em.persist(comentario);
	}

	public void atualizar(Comentario comentario) {
		comentario.setData(Calendar.getInstance());
		em.merge(comentario);
	}

	public void remover(int id){
		Comentario comentario = consultar(id);
		em.remove(comentario);
	}

	public Comentario consultar(int id)  {
		return em.find(Comentario.class, id);
	}

	public List<Comentario> pesquisar(ComentarioSeletor seletor){
		StringBuilder jpql = new StringBuilder();
		jpql.append("SELECT c FROM Comentario c ");
		jpql.append("JOIN c.usuario ");
		jpql.append("JOIN c.tweet t ");
		jpql.append("JOIN t.usuario ");
		
		this.criarFiltros(jpql, seletor);

		Query query = em.createQuery(jpql.toString());
		this.adicionarParametros(query, seletor);

		return query.getResultList();
	}

	public Long contar(ComentarioSeletor seletor) {
		StringBuilder jpql = new StringBuilder();
		jpql.append("SELECT COUNT(c) FROM Comentario c");
		
		this.criarFiltros(jpql, seletor);

		Query query = em.createQuery(jpql.toString());
		this.adicionarParametros(query, seletor);

		return (Long) query.getSingleResult();
	}

	public List<Comentario> listarTodos() {
		return this.pesquisar(new ComentarioSeletor());
	}

	private void criarFiltros(StringBuilder jpql, ComentarioSeletor seletor) {

		if (seletor.possuiFiltro()) {
			jpql.append(" WHERE ");
			boolean filtros = false;
			if (seletor.getId() != null) {
				jpql.append(" c.id = :id ");
				filtros = true;

			}
			if (seletor.getConteudo() != null && !seletor.getConteudo().trim().isEmpty()) {
				if (filtros) {
					jpql.append("AND ");
				} else {
					filtros = true;
				}
				jpql.append(" c.conteudo LIKE :conteudo ");
			}
			if (seletor.getData() != null) {
				if (filtros) {
					jpql.append("AND ");
				} else {
					filtros = true;
				}
				jpql.append(" date(c.data) = :data_postagem ");
			}
			if (seletor.getIdUsuario() != null) {
				if (filtros) {
					jpql.append("AND ");
				} else {
					filtros = true;
				}
				jpql.append(" c.usuario.id = :id_usuario ");
			}
			if (seletor.getIdTweet() != null) {
				if (filtros) {
					jpql.append("AND ");
				}
				jpql.append(" c.tweet.id = :id_tweet ");
			}

		}
	}

	private void adicionarParametros(Query query, ComentarioSeletor seletor)  {
		if (seletor.getId() != null) {
			query.setParameter("id", seletor.getId());
		}

		if (seletor.getIdTweet() != null) {
			query.setParameter("id_tweet", seletor.getIdTweet());
		}

		if (seletor.getIdUsuario() != null) {
			query.setParameter("id_usuario", seletor.getIdUsuario());
		}

		if (seletor.getConteudo() != null && !seletor.getConteudo().trim().isEmpty()) {
			query.setParameter("conteudo", String.format("%%%s%%", seletor.getConteudo()));
		}

		if (seletor.getData() != null) {
			query.setParameter("data_postagem", seletor.getData());
		}
	}

}
